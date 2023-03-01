package run.gleam.lang.core.stubs

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.tree.IStubFileElementType
import run.gleam.lang.GleamLanguage


abstract class GleamStubElementType<StubT : StubElement<*>, PsiT : PsiElement>(
    debugName: String
) : IStubElementType<StubT, PsiT>(debugName, GleamLanguage) {

    final override fun getExternalId(): String = "gleam.${super.toString()}"

    override fun indexStub(stub: StubT, sink: IndexSink) {}
}

fun createStubIfParentIsStub(node: ASTNode): Boolean {
    val parent = node.treeParent
    val parentType = parent.elementType
    return (parentType is IStubElementType<*, *> && parentType.shouldCreateStub(parent)) ||
            parentType is IStubFileElementType<*>
}