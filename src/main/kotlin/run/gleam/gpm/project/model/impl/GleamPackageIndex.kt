package run.gleam.gpm.project.model.impl

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.indexing.LightDirectoryIndex
import run.gleam.gpm.project.model.GleamProject
import run.gleam.gpm.project.workspace.GleamWorkspace.Package
import run.gleam.gpm.project.model.GleamProjectsService
import run.gleam.gpm.project.model.GleamProjectsService.Companion.GLEAM_PROJECTS_TOPIC
import run.gleam.openapiext.checkReadAccessAllowed
import run.gleam.openapiext.checkWriteAccessAllowed
import java.util.*

class GleamPackageIndex(
    private val project: Project,
    private val service: GleamProjectsService
) : GleamProjectsService.GleamProjectsListener {

    private val indices: MutableMap<GleamProject, LightDirectoryIndex<Optional<Package>>> = hashMapOf()
    private var indexDisposable: Disposable? = null

    init {
        project.messageBus.connect(project).subscribe(GLEAM_PROJECTS_TOPIC, this)
    }

    override fun gleamProjectsUpdated(service: GleamProjectsService, projects: Collection<GleamProject>) {
        checkWriteAccessAllowed()
        resetIndex()
        val disposable = Disposer.newDisposable("GleamPackageIndexDisposable")
        Disposer.register(project, disposable)
        for (GleamProject in projects) {
            val packages = GleamProject.workspace?.packages.orEmpty()
            indices[GleamProject] = LightDirectoryIndex(disposable, Optional.empty()) { index ->
                for (pkg in packages) {
                    val info = Optional.of(pkg)
                    index.putInfo(pkg.contentRoot, info)
                    index.putInfo(pkg.outDir, info)
                    index.putInfo(pkg.target.crateRoot?.parent, info)
                }
            }
        }
        indexDisposable = disposable
    }

    fun findPackageForFile(file: VirtualFile): Package? {
        checkReadAccessAllowed()
        val GleamProject = service.findProjectForFile(file) ?: return null
        return indices[GleamProject]?.getInfoForFile(file)?.orElse(null)
    }

    private fun resetIndex() {
        val disposable = indexDisposable
        if (disposable != null) {
            Disposer.dispose(disposable)
        }
        indexDisposable = null
        indices.clear()
    }
}
