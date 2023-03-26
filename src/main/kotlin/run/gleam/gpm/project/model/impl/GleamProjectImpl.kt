package run.gleam.gpm.project.model.impl

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.execution.RunManager
import com.intellij.ide.impl.isTrusted
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.invokeAndWaitIfNeeded
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.components.*
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ex.ProjectEx
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.roots.ex.ProjectRootManagerEx
import com.intellij.openapi.util.EmptyRunnable
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.util.indexing.LightDirectoryIndex
import com.intellij.util.io.exists
import com.intellij.util.io.systemIndependentPath
import org.jdom.Element
import org.jetbrains.annotations.TestOnly
import run.gleam.gpm.GleamPMConstants
import run.gleam.gpm.project.model.*
import run.gleam.gpm.project.model.GleamProjectsService.Companion.GLEAM_PROJECTS_REFRESH_TOPIC
import run.gleam.gpm.project.model.GleamProjectsService.Companion.GLEAM_PROJECTS_TOPIC
import run.gleam.gpm.project.model.GleamProjectsService.GleamPMRefreshStatus
import run.gleam.gpm.project.workspace.GleamWorkspace
import run.gleam.gpm.project.workspace.PackageOrigin
import run.gleam.openapiext.TaskResult
import run.gleam.openapiext.isUnitTestMode
import run.gleam.openapiext.modules
import run.gleam.openapiext.pathAsPath
import run.gleam.stdext.AsyncValue
import run.gleam.stdext.applyWithSymlink
import run.gleam.taskQueue
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException
import java.util.concurrent.atomic.AtomicReference

@State(name= "GleamProjects", storages = [
    Storage(StoragePathMacros.WORKSPACE_FILE),
    Storage("misc.xml", deprecated = true)
])
open class GleamProjectsServiceImpl(
    final override val project: Project
) : GleamProjectsService, PersistentStateComponent<Element>, Disposable {
    /**
     * The heart of the plugin Project model. Care must be taken to ensure
     * this is thread-safe, and that refreshes are scheduled after
     * set of projects changes.
     */
    private val projects = AsyncValue<List<GleamProjectImpl>>(emptyList())

    @Suppress("LeakingThis")
    private val noProjectMarker = GleamProjectImpl(Paths.get(""), this)

    /**
     * [directoryIndex] allows to quickly map from a [VirtualFile] to
     * a containing [GleamProject].
     */
    private val directoryIndex: LightDirectoryIndex<GleamProjectImpl> =
        LightDirectoryIndex(project, noProjectMarker) { index ->
            val visited = mutableSetOf<VirtualFile>()

            fun VirtualFile.put(gleamProject: GleamProjectImpl) {
                if (this in visited) return
                visited += this
                index.putInfo(this, gleamProject)
            }

            fun GleamWorkspace.Package.put(gleamProject: GleamProjectImpl) {
                contentRoot?.put(gleamProject)
                outDir?.put(gleamProject)
                target.crateRoot?.parent?.put(gleamProject)
            }

            val lowPriority = mutableListOf<Pair<GleamWorkspace.Package, GleamProjectImpl>>()

            for (gleamProject in projects.currentState) {
                gleamProject.rootDir?.put(gleamProject)
                for (pkg in gleamProject.workspace?.packages.orEmpty()) {
                    if (pkg.origin == PackageOrigin.WORKSPACE) {
                        pkg.put(gleamProject)
                    } else {
                        lowPriority += pkg to gleamProject
                    }
                }
            }

            for ((pkg, gleamProject) in lowPriority) {
                pkg.put(gleamProject)
            }
        }

    @Suppress("LeakingThis")
    private val packageIndex: GleamPackageIndex = GleamPackageIndex(project, this)

    override val allProjects: Collection<GleamProject>
        get() = projects.currentState

    override val hasAtLeastOneValidProject: Boolean
        get() = hasAtLeastOneValidProject(allProjects)

    // Guarded by the platform RWLock
    override var initialized: Boolean = false

    override fun findProjectForFile(file: VirtualFile): GleamProject? =
        file.applyWithSymlink { directoryIndex.getInfoForFile(it).takeIf { info -> info !== noProjectMarker } }

    override fun findPackageForFile(file: VirtualFile): GleamWorkspace.Package? =
        file.applyWithSymlink(packageIndex::findPackageForFile)

    override fun attachGleamProject(manifest: Path): Boolean {
        if (isExistingProject(allProjects, manifest)) return false
        modifyProjects { projects ->
            if (isExistingProject(projects, manifest))
                CompletableFuture.completedFuture(projects)
            else
                doRefresh(project, projects + GleamProjectImpl(manifest, this))
        }
        return true
    }

    override fun attachGleamProjects(vararg manifests: Path) {
        val manifests2 = manifests.filter { !isExistingProject(allProjects, it) }
        if (manifests2.isEmpty()) return
        modifyProjects { projects ->
            val newManifests3 = manifests2.filter { !isExistingProject(projects, it) }
            if (newManifests3.isEmpty())
                CompletableFuture.completedFuture(projects)
            else
                doRefresh(project, projects + newManifests3.map { GleamProjectImpl(it, this) })
        }
    }

    override fun detachGleamProject(gleamProject: GleamProject) {
        modifyProjects { projects ->
            CompletableFuture.completedFuture(projects.filter { it.manifest != gleamProject.manifest })
        }
    }

    override fun refreshAllProjects(): CompletableFuture<out List<GleamProject>> =
        modifyProjects { doRefresh(project, it) }

    override fun discoverAndRefresh(): CompletableFuture<out List<GleamProject>> {
        val guessManifest = suggestManifests().firstOrNull()
            ?: return CompletableFuture.completedFuture(projects.currentState)

        return modifyProjects { projects ->
            if (hasAtLeastOneValidProject(projects)) return@modifyProjects CompletableFuture.completedFuture(projects)
            doRefresh(project, listOf(GleamProjectImpl(guessManifest.pathAsPath, this)))
        }
    }

    override fun suggestManifests(): Sequence<VirtualFile> =
        project.modules
            .asSequence()
            .flatMap { ModuleRootManager.getInstance(it).contentRoots.asSequence() }
            .mapNotNull { it.findChild(GleamPMConstants.MANIFEST_FILE) }

    /**
     * All modifications to project model except for low-level `loadState` should
     * go through this method: it makes sure that when we update various IDEA listeners,
     * [allProjects] contains fresh projects.
     */
    protected fun modifyProjects(
        updater: (List<GleamProjectImpl>) -> CompletableFuture<List<GleamProjectImpl>>
    ): CompletableFuture<List<GleamProjectImpl>> {
        val refreshStatusPublisher = project.messageBus.syncPublisher(GLEAM_PROJECTS_REFRESH_TOPIC)

        val wrappedUpdater = { projects: List<GleamProjectImpl> ->
            refreshStatusPublisher.onRefreshStarted()
            updater(projects)
        }

        return projects.updateAsync(wrappedUpdater)
            .thenApply { projects ->
                invokeAndWaitIfNeeded {
                    val fileTypeManager = FileTypeManager.getInstance()
                    runWriteAction {
                        directoryIndex.resetIndex()
                        // In unit tests roots change is done by the test framework in most cases
                        runWithNonLightProject(project) {
                            ProjectRootManagerEx.getInstanceEx(project)
                                .makeRootsChange(EmptyRunnable.getInstance(), false, true)
                        }
                        project.messageBus.syncPublisher(GLEAM_PROJECTS_TOPIC)
                            .gleamProjectsUpdated(this, projects)
                        initialized = true
                    }
                }
                projects
            }.handle { projects, err ->
                val status = err?.toRefreshStatus() ?: GleamPMRefreshStatus.SUCCESS
                refreshStatusPublisher.onRefreshFinished(status)
                projects
            }
    }

    private fun Throwable.toRefreshStatus(): GleamPMRefreshStatus {
        return when {
            this is ProcessCanceledException -> GleamPMRefreshStatus.CANCEL
            this is CompletionException && cause is ProcessCanceledException -> GleamPMRefreshStatus.CANCEL
            else -> GleamPMRefreshStatus.FAILURE
        }
    }

    private fun modifyProjectsLite(
        f: (List<GleamProjectImpl>) -> List<GleamProjectImpl>
    ): CompletableFuture<List<GleamProjectImpl>> =
        projects.updateSync(f)
            .thenApply { projects ->
                invokeAndWaitIfNeeded {
                    val psiManager = PsiManager.getInstance(project)
                    runWriteAction {
                        directoryIndex.resetIndex()
                        project.messageBus.syncPublisher(GLEAM_PROJECTS_TOPIC)
                            .gleamProjectsUpdated(this, projects)
                        psiManager.dropPsiCaches()
                        DaemonCodeAnalyzer.getInstance(project).restart()
                    }
                }

                projects
            }

    override fun getState(): Element {
        val state = Element("state")
        for (gleamProject in allProjects) {
            val gleamProjectElement = Element("gleamProject")
            gleamProjectElement.setAttribute("FILE", gleamProject.manifest.systemIndependentPath)
            state.addContent(gleamProjectElement)
        }

        // Note that if [state] is empty (there are no gleam projects), [noStateLoaded] will be called on the next load

        return state
    }

    override fun loadState(state: Element) {
        // [gleamProjects] is non-empty here. Otherwise, [noStateLoaded] is called instead of [loadState]
        val gleamProjects = state.getChildren("gleamProject")
        val loaded = mutableListOf<GleamProjectImpl>()

        for (gleamProject in gleamProjects) {
            val file = gleamProject.getAttributeValue("FILE")
            val manifest = Paths.get(file)
            val newProject = GleamProjectImpl(manifest, this)
            loaded.add(newProject)
        }

        // Refresh projects via `invokeLater` to avoid model modifications
        // while the project is being opened. Use `updateSync` directly
        // instead of `modifyProjects` for this reason
        projects.updateSync { loaded }
            .whenComplete { _, _ ->
                val disableRefresh = System.getProperty(GLEAM_DISABLE_PROJECT_REFRESH_ON_CREATION, "false").toBooleanStrictOrNull()
                if (disableRefresh != true) {
                    invokeLater {
                        if (project.isDisposed) return@invokeLater
                        refreshAllProjects()
                    }
                }
            }
    }

    /**
     * Note that [noStateLoaded] is called not only during the first service creation, but on any
     * service load if [getState] returned empty state during previous save (i.e. there are no gleam project)
     */
    override fun noStateLoaded() {
        // Do nothing: in theory, we might try to do [discoverAndRefresh]
        // here, but the `RsToolchain` is most likely not ready.
        //
        // So the actual "Let's guess a project model if it is not imported
        // explicitly" happens in [org.rust.ide.notifications.MissingToolchainNotificationProvider]

        initialized = true // No lock required b/c it's service init time

        // // Should be initialized with this service because it stores a part of gleam projects data
        // project.service<UserDisabledFeaturesHolder>()
    }

    override fun dispose() {}

    override fun toString(): String =
        "GleamProjectsService(projects = $allProjects)"

    companion object {
        const val GLEAM_DISABLE_PROJECT_REFRESH_ON_CREATION: String = "gleam.disable.project.refresh.on.creation"
    }
}

data class GleamProjectImpl(
    override val manifest: Path,
    private val projectService: GleamProjectsServiceImpl,
    val rawWorkspace: GleamWorkspace? = null,
    override val gleamInfo: GleamInfo? = null,
    override val workspaceStatus: GleamProject.UpdateStatus = GleamProject.UpdateStatus.NeedsUpdate,
    override val gleamInfoStatus: GleamProject.UpdateStatus = GleamProject.UpdateStatus.NeedsUpdate
) : UserDataHolderBase(), GleamProject {
    override val project: Project = projectService.project

    override val workspace: GleamWorkspace? by lazy(LazyThreadSafetyMode.PUBLICATION) {
        val rawWorkspace = rawWorkspace ?: return@lazy null
            rawWorkspace
    }

    override val presentableName: String by lazy {
        workspace?.packages?.singleOrNull {
            it.origin == PackageOrigin.WORKSPACE && it.rootDirectory == workingDirectory
        }?.name ?: workingDirectory.fileName.toString()
    }

    private val rootDirCache = AtomicReference<VirtualFile>()

    override val rootDir: VirtualFile?
        get() {
            val cached = rootDirCache.get()
            if (cached != null && cached.isValid) return cached
            val file = LocalFileSystem.getInstance().findFileByIoFile(workingDirectory.toFile())
            rootDirCache.set(file)
            return file
        }

    override val workspaceRootDir: VirtualFile? get() = rawWorkspace?.workspaceRoot

    @TestOnly
    fun setRootDir(dir: VirtualFile) = rootDirCache.set(dir)

    fun withWorkspace(result: TaskResult<GleamWorkspace>): GleamProjectImpl = when (result) {
        is TaskResult.Ok -> copy(
            rawWorkspace = result.value,
            workspaceStatus = GleamProject.UpdateStatus.UpToDate,
        )
        is TaskResult.Err -> copy(workspaceStatus = GleamProject.UpdateStatus.UpdateFailed(result.reason))
    }

    fun withGleamInfo(result: TaskResult<GleamInfo>): GleamProjectImpl = when (result) {
        is TaskResult.Ok -> copy(gleamInfo = result.value, gleamInfoStatus = GleamProject.UpdateStatus.UpToDate)
        is TaskResult.Err -> copy(gleamInfoStatus = GleamProject.UpdateStatus.UpdateFailed(result.reason))
    }

    override fun toString(): String =
        "GleamProject(manifest = $manifest)"
}

val GleamProject.workingDirectory: Path get() = manifest.parent

val GleamProjectsService.allPackages: Sequence<GleamWorkspace.Package>
    get() = allProjects.asSequence().mapNotNull { it.workspace }.flatMap { it.packages.asSequence() }

private fun hasAtLeastOneValidProject(projects: Collection<GleamProject>) =
    projects.any { it.manifest.exists() }

/** Keep in sync with [run.gleam.gpm.project.model.impl.deduplicateProjects] */
private fun isExistingProject(projects: Collection<GleamProject>, manifest: Path): Boolean {
    if (projects.any { it.manifest == manifest }) return true
    return projects.mapNotNull { it.workspace }.flatMap { it.packages }
        .filter { it.origin == PackageOrigin.WORKSPACE }
        .any { it.rootDirectory == manifest.parent }
}

private fun doRefresh(project: Project, projects: List<GleamProjectImpl>): CompletableFuture<List<GleamProjectImpl>> {
    @Suppress("UnstableApiUsage")
    if (!project.isTrusted()) return CompletableFuture.completedFuture(projects)
    // TODO: get rid of `result` here
    val result = if (projects.isEmpty()) {
        CompletableFuture.completedFuture(emptyList())
    } else {
        val result = CompletableFuture<List<GleamProjectImpl>>()
        val syncTask = GleamPMSyncTask(project, projects, result)
        project.taskQueue.run(syncTask)
        result
    }

    return result.thenApply { updatedProjects ->
        runWithNonLightProject(project) {
            setupProjectRoots(project, updatedProjects)
        }
        updatedProjects
    }
}

private inline fun runWithNonLightProject(project: Project, action: () -> Unit) {
    if ((project as? ProjectEx)?.isLight != true) {
        action()
    } else {
        check(isUnitTestMode)
    }
}

private fun setupProjectRoots(project: Project, gleamProjects: List<GleamProject>) {
    invokeAndWaitIfNeeded {
        // Initialize services that we use (probably indirectly) in write action below.
        // Otherwise, they can be initialized in write action that may lead to deadlock
        RunManager.getInstance(project)
        ProjectFileIndex.getInstance(project)

        runWriteAction {
            if (project.isDisposed) return@runWriteAction
            ProjectRootManagerEx.getInstanceEx(project).mergeRootsChangesDuring {
                for (gleamProject in gleamProjects) {
                    gleamProject.workspaceRootDir?.setupContentRoots(project) { contentRoot ->
                        addExcludeFolder("${contentRoot.url}/${GleamPMConstants.ProjectLayout.target}")
                    }

//                    if ((gleamProject as? GleamProjectImpl)?.doesProjectLooksLikeRustc() == true) {
//                        gleamProject.workspaceRootDir?.setupContentRoots(project) { contentRoot ->
//                            addExcludeFolder("${contentRoot.url}/build")
//                        }
//                    }

                    val workspacePackages = gleamProject.workspace?.packages
                        .orEmpty()
                        .filter { it.origin == PackageOrigin.WORKSPACE }

                    for (pkg in workspacePackages) {
                        pkg.contentRoot?.setupContentRoots(project, ContentEntryWrapper::setup)
                    }
                }
            }
        }
    }
}

private fun VirtualFile.setupContentRoots(project: Project, setup: ContentEntryWrapper.(VirtualFile) -> Unit) {
    val packageModule = ModuleUtilCore.findModuleForFile(this, project) ?: return
    setupContentRoots(packageModule, setup)
}

private fun VirtualFile.setupContentRoots(packageModule: Module, setup: ContentEntryWrapper.(VirtualFile) -> Unit) {
    ModuleRootModificationUtil.updateModel(packageModule) { rootModel ->
        val contentEntry = rootModel.contentEntries.singleOrNull() ?: return@updateModel
        ContentEntryWrapper(contentEntry).setup(this)
    }
}























