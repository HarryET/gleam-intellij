package run.gleam.lang.core.psi.ext

import com.intellij.psi.search.SearchScope
import com.intellij.psi.util.PsiTreeUtil
import run.gleam.lang.core.psi.GleamFunction
import run.gleam.lang.core.psi.GleamFunctionBody

val GleamFunction.body: GleamFunctionBody? get() = PsiTreeUtil.getChildOfType(this, GleamFunctionBody::class.java)

val GleamFunction.declaration: String
    get() = buildString {
        val visibilityMod = visibilityModifier
        if (visibilityMod != null)
            append(visibilityMod.text)
        append(identifier.text)
        val params = functionParameters
        if (params != null)
            append(params.text)
        val returnType = functionReturnType
        if (returnType != null)
            append(returnType.text)
    }

fun GleamFunction.findUsages(scope: SearchScope? = null): Sequence<Unit> = searchReferences(scope)
    .asSequence().mapNotNull {}