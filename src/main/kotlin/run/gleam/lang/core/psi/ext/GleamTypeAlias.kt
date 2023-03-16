package run.gleam.lang.core.psi.ext

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IStubElementType
import run.gleam.lang.core.psi.GleamTypeAlias
import run.gleam.lang.core.psi.GleamTypes
import run.gleam.lang.core.psi.GleamTypes.TYPE_NAME
import run.gleam.lang.core.stubs.GleamTypeAliasStub

abstract class GleamTypeAliasImplMixIn : GleamStubbedNamedVisibilityElementImpl<GleamTypeAliasStub>, GleamTypeAlias {
    constructor(node: ASTNode): super(node)

    constructor(stub: GleamTypeAliasStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun getNameIdentifier(): PsiElement? {
        // todo: TYPE_NAME is not accurate, because it can be Thing(foo), need to make this more accurate to only select Thing
        return findChildByType(TYPE_NAME) ?: super.getNameIdentifier()
    }
}

val GleamTypeAlias.isOpaque: Boolean
    get() = greenStub?.isOpaque ?: (node.findChildByType(GleamTypes.OPACITY_MODIFIER) != null)
