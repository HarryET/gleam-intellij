package run.gleam.lang.core.psi.ext

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IStubElementType
import run.gleam.lang.core.psi.GleamTypeDefinition
import run.gleam.lang.core.psi.GleamTypes.TYPE_NAME
import run.gleam.lang.core.stubs.GleamTypeDefStub

abstract class GleamTypeDefinitionImplMixIn : GleamStubbedNamedVisibilityElementImpl<GleamTypeDefStub>, GleamTypeDefinition {
    constructor(node: ASTNode): super(node)

    constructor(stub: GleamTypeDefStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun getNameIdentifier(): PsiElement? {
        return findChildByType(TYPE_NAME) ?: super.getNameIdentifier()
    }
}