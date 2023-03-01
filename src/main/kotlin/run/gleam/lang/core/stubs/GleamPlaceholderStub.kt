package run.gleam.lang.core.stubs

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.*

open class GleamPlaceholderStub<PsiT : PsiElement>(parent: StubElement<*>?, elementType: IStubElementType<*, *>)
    : StubBase<PsiT>(parent, elementType) {

    open class Type<PsiT : PsiElement>(
        debugName: String,
        private val psiCtor: (GleamPlaceholderStub<*>, IStubElementType<*, *>) -> PsiT
    ) : GleamStubElementType<GleamPlaceholderStub<*>, PsiT>(debugName) {

        override fun shouldCreateStub(node: ASTNode): Boolean = createStubIfParentIsStub(node)

        override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): GleamPlaceholderStub<PsiT>
                = GleamPlaceholderStub(parentStub, this)

        override fun serialize(stub: GleamPlaceholderStub<*>, dataStream: StubOutputStream) {}

        override fun createPsi(stub: GleamPlaceholderStub<*>): PsiT = psiCtor(stub, this)

        override fun createStub(psi: PsiT, parentStub: StubElement<*>?): GleamPlaceholderStub<PsiT>
                = GleamPlaceholderStub(parentStub, this)
    }
}