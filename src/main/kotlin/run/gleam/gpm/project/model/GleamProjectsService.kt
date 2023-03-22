package run.gleam.gpm.project.model

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.components.service
import com.intellij.openapi.roots.ContentEntry
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.util.UserDataHolderEx
import com.intellij.util.messages.Topic
import run.gleam.gpm.GleamPMConstants
import run.gleam.gpm.project.workspace.GleamWorkspace
import java.nio.file.Path
import java.util.concurrent.CompletableFuture

interface GleamProjectsService {
    val project: Project
    val allProjects: Collection<GleamProject>
    val hasAtLeastOneValidProject: Boolean
    val initialized: Boolean

    fun findProjectForFile(file: VirtualFile): GleamProject?
    fun findPackageForFile(file: VirtualFile): GleamWorkspace.Package?

    /**
     * @param manifest a path to `gleam.toml` file of the project that should be attached
     */
    fun attachGleamProject(manifest: Path): Boolean
    fun attachGleamProjects(vararg manifests: Path)
    fun refreshAllProjects(): CompletableFuture<out List<GleamProject>>
    fun discoverAndRefresh(): CompletableFuture<out List<GleamProject>>
    fun suggestManifests(): Sequence<VirtualFile>

    companion object {
        val GLEAM_PROJECTS_TOPIC: Topic<GleamProjectsListener> = Topic(
            "cargo projects changes",
            GleamProjectsListener::class.java
        )

        val GLEAM_PROJECTS_REFRESH_TOPIC: Topic<GleamProjectsRefreshListener> = Topic(
            "Cargo refresh",
            GleamProjectsRefreshListener::class.java
        )
    }

    fun interface GleamProjectsListener {
        fun cargoProjectsUpdated(service: GleamProjectsService, projects: Collection<GleamProject>)
    }

    interface GleamProjectsRefreshListener {
        fun onRefreshStarted()
        fun onRefreshFinished(status: GleamPMRefreshStatus)
    }

    enum class GleamPMRefreshStatus {
        SUCCESS,
        FAILURE,
        CANCEL
    }
}

val Project.gleamProjects: GleamProjectsService get() = service()

fun GleamProjectsService.isGeneratedFile(file: VirtualFile): Boolean {
    val outDir = findPackageForFile(file)?.outDir ?: return false
    return com.intellij.openapi.vfs.VfsUtil.isAncestor(outDir, file, false)
}

interface GleamProject : UserDataHolderEx {
    val project: Project
    val manifest: Path
    val rootDir: VirtualFile?
    val workspaceRootDir: VirtualFile?

    val presentableName: String
    val workspace: GleamWorkspace?

    val gleamInfo: GleamInfo?

    val workspaceStatus: UpdateStatus
    val gleamInfoStatus: UpdateStatus

    val mergedStatus: UpdateStatus
        get() = workspaceStatus
            .merge(gleamInfoStatus)

    sealed class UpdateStatus(private val priority: Int) {
        object UpToDate: UpdateStatus(0)
        object NeedsUpdate: UpdateStatus(1)
        class UpdateFailed(@Suppress("UnstableApiUsage") @NlsContexts.Tooltip val reason: String) : UpdateStatus(2)
        fun merge(status: UpdateStatus): UpdateStatus = if (priority >= status.priority) this else status
    }
}

data class GleamInfo(
    val version: String?,
    val targets: List<String>? = null
)

fun guessAndSetupGleamProject(project: Project, explicitRequest: Boolean = false): Boolean {
    if (!explicitRequest) {
        val alreadyTried = run {
            val key = "run.gleam.gpm.project.model.PROJECT_DISCOVERY"
            val properties = PropertiesComponent.getInstance(project)
            val alreadyTried = properties.getBoolean(key)
            properties.setValue(key, true)
            alreadyTried
        }
        if (alreadyTried) return false
    }

    if (!project.gleamProjects.hasAtLeastOneValidProject) {
        project.gleamProjects.discoverAndRefresh()
        return true
    }
    return false
}

fun ContentEntry.setup(contentRoot: VirtualFile) = ContentEntryWrapper(this).setup(contentRoot)

fun ContentEntryWrapper.setup(contentRoot: VirtualFile) {
    val makeVfsUrl = { dirName: String -> contentRoot.findChild(dirName)?.url }
    GleamPMConstants.ProjectLayout.sources.mapNotNull(makeVfsUrl).forEach {
        addSourceFolder(it, isTestSource = false)
    }
    GleamPMConstants.ProjectLayout.tests.mapNotNull(makeVfsUrl).forEach {
        addSourceFolder(it, isTestSource = true)
    }
    makeVfsUrl(GleamPMConstants.ProjectLayout.target)?.let(::addExcludeFolder)
}

class ContentEntryWrapper(private val contentEntry: ContentEntry) {
    private val knownFolders: Set<String> = contentEntry.knownFolders()

    fun addExcludeFolder(url: String) {
        if (url in knownFolders) return
        contentEntry.addExcludeFolder(url)
    }

    fun addSourceFolder(url: String, isTestSource: Boolean) {
        if (url in knownFolders) return
        contentEntry.addSourceFolder(url, isTestSource)
    }

    private fun ContentEntry.knownFolders(): Set<String> {
        val knownRoots = sourceFolders.mapTo(hashSetOf()) { it.url }
        knownRoots += excludeFolderUrls
        return knownRoots
    }
}