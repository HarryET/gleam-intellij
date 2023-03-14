package run.gleam.lang.core.psi.ext

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.tree.IElementType
import run.gleam.lang.core.psi.GleamTypes.IDENTIFIER
import run.gleam.lang.core.stubs.GleamNamedStub

interface GleamNamedElement : GleamElement, PsiNamedElement, NavigatablePsiElement

interface GleamNameIdentifierOwner : GleamNamedElement, PsiNameIdentifierOwner

//val GleamNamedElement.escapedName: String? get() = name?.escapeIdentifierIfNeeded()

abstract class GleamNamedElementImpl(type: IElementType) : GleamElementImpl(type),
    GleamNameIdentifierOwner {

    override fun getNameIdentifier(): PsiElement? = findChildByType(IDENTIFIER)?.psi

//    override fun getName(): String? = nameIdentifier?.unescapedText
    override fun getName(): String? = nameIdentifier?.text

    override fun setName(name: String): PsiElement? {
//        nameIdentifier?.replace(RsPsiFactory(project).createIdentifier(name))
        return this
    }

    override fun getTextOffset(): Int = nameIdentifier?.textOffset ?: super.getTextOffset()
}

abstract class GleamStubbedNamedElementImpl<StubT> : GleamStubbedElementImpl<StubT>,
    GleamNameIdentifierOwner
        where StubT : GleamNamedStub, StubT : StubElement<*> {

    constructor(node: ASTNode) : super(node)

    constructor(stub: StubT, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun getNameIdentifier(): PsiElement? = findChildByType(IDENTIFIER)

    override fun getName(): String? {
        val stub = greenStub
//        return if (stub !== null) stub.name else nameIdentifier?.unescapedText
        return if (stub !== null) stub.name else nameIdentifier?.text
    }

    override fun setName(name: String): PsiElement? {
//        nameIdentifier?.replace(RsPsiFactory(project).createIdentifier(name))
        return this
    }

    override fun getTextOffset(): Int = nameIdentifier?.textOffset ?: super.getTextOffset()
}


abstract class GleamStubbedNamedVisibilityElementImpl<StubT> : GleamStubbedElementImpl<StubT>, GleamVisibilityOwner,
    GleamNameIdentifierOwner
        where StubT : GleamNamedStub, StubT : StubElement<*> {

    constructor(node: ASTNode) : super(node)

    constructor(stub: StubT, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun getNameIdentifier(): PsiElement? = findChildByType(IDENTIFIER)

    override fun getName(): String? {
        val stub = greenStub
//        return if (stub !== null) stub.name else nameIdentifier?.unescapedText
        return if (stub !== null) stub.name else nameIdentifier?.text
    }

    override fun setName(name: String): PsiElement? {
//        nameIdentifier?.replace(RsPsiFactory(project).createIdentifier(name))
        return this
    }

    override fun getTextOffset(): Int = nameIdentifier?.textOffset ?: super.getTextOffset()
}