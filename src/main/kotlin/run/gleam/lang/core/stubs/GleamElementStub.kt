package run.gleam.lang.core.stubs

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement

abstract class GleamElementStub<PsiT : PsiElement>(
    parent: StubElement<*>?, elementType: IStubElementType<out StubElement<*>, *>?
) : StubBase<PsiT>(parent, elementType)