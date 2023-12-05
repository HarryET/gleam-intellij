package run.gleam.ide.navigation

import com.intellij.navigation.ChooseByNameContributorEx
import com.intellij.navigation.GotoClassContributor
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.logger
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.stubs.StubIndexKey
import com.intellij.util.Processor
import com.intellij.util.indexing.FindSymbolParameters
import com.intellij.util.indexing.IdFilter
//import run.gleam.lang.core.psi.ext.GleamNamedElement
import run.gleam.openapiext.isInternal

//abstract class GleamNavigationContributorBase<T> protected constructor(
//    private val indexKey: StubIndexKey<String, T>,
//    private val clazz: Class<T>
//) : ChooseByNameContributorEx,
//    GotoClassContributor where T : NavigationItem, T : GleamNamedElement {
//
//    override fun processNames(processor: Processor<in String>, scope: GlobalSearchScope, filter: IdFilter?) {
//        checkFilter(filter)
//        StubIndex.getInstance().processAllKeys(
//            indexKey,
//            processor,
//            scope,
//            null
//        )
//    }
//
//    override fun processElementsWithName(name: String, processor: Processor<in NavigationItem>, parameters: FindSymbolParameters) {
//        checkFilter(parameters.idFilter)
//        val originScope = parameters.searchScope
//        StubIndex.getInstance().processElements(
//            indexKey,
//            name,
//            parameters.project,
//            originScope,
//            null,
//            clazz
//        ) { true }
//    }
//
//    override fun getQualifiedName(item: NavigationItem): String? = item.name
//
//    override fun getQualifiedNameSeparator(): String = "."
//}
//
//private val LOG: Logger = logger<GleamNavigationContributorBase<*>>()
//
//private fun checkFilter(filter: IdFilter?) {
//    if (isInternal && filter != null) {
//        LOG.error("IdFilter is supposed to be null", Throwable())
//    }
//}