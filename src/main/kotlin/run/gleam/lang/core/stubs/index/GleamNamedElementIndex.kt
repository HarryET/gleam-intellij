package run.gleam.lang.core.stubs.index

import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndexKey
import run.gleam.lang.core.psi.ext.GleamNamedElement
import run.gleam.lang.core.stubs.GleamFileStub
import run.gleam.openapiext.checkCommitIsNotInProgress
import run.gleam.openapiext.getElements

class GleamNamedElementIndex : StringStubIndexExtension<GleamNamedElement>() {
    override fun getVersion(): Int = GleamFileStub.Type.stubVersion
    override fun getKey(): StubIndexKey<String, GleamNamedElement> = KEY

    companion object {
        val KEY: StubIndexKey<String, GleamNamedElement> =
            StubIndexKey.createIndexKey("run.gleam.lang.core.stubs.index.GleamNamedElementIndex")

        fun findElementsByName(
            project: Project,
            target: String,
            scope: GlobalSearchScope = GlobalSearchScope.allScope(project)
        ): Collection<GleamNamedElement> {
            checkCommitIsNotInProgress(project)
            return getElements(KEY, target, project, scope)
        }
    }
}