package run.gleam.ide.highlight

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import org.antlr.intellij.adaptor.lexer.TokenIElementType

class GleamAnnotator: Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element.elementType !is TokenIElementType) return
        val elementType = (element.elementType as TokenIElementType)
        when (elementType.antlrTokenType) {
            GleamLexer.FN -> annotateFn(element, holder)
            else -> {}
        }
    }

    companion object {
        fun annotateFn(element: PsiElement, holder: AnnotationHolder) {
            
        }
    }
}