package run.gleam.lang.core.psi.ext

import com.intellij.openapi.util.UserDataHolderEx
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.tree.CompositePsiElement
import com.intellij.psi.tree.IElementType
import run.gleam.lang.core.psi.GleamTokens

interface GleamElement : PsiElement, UserDataHolderEx {}
abstract class GleamElementImpl(type: IElementType): CompositePsiElement(type), GleamElement {
    override fun toString(): String = "${javaClass.simpleName}($elementType)"
}

/**
 * Delete the element along with a neighbour comma.
 * If a comma follows the element, it will be deleted.
 * Else if a comma precedes the element, it will be deleted.
 *
 * It is useful to remove elements that are parts of comma separated lists (parameters, arguments, use specks, ...).
 */
//fun GleamElement.deleteWithSurroundingComma() {
//    val followingComma = getNextNonCommentSibling()
//    if (followingComma?.elementType == GleamTokens.COMMA) {
//        followingComma?.delete()
//    } else {
//        val precedingComma = getPrevNonCommentSibling()
//        if (precedingComma?.elementType == GleamTokens.COMMA) {
//            precedingComma?.delete()
//        }
//    }
//
//    delete()
//}

/**
 * Delete the element along with all surrounding whitespace and a single surrounding comma.
 * See [deleteWithSurroundingComma].
 */
//fun GleamElement.deleteWithSurroundingCommaAndWhitespace() {
//    val toDelete = rightSiblings.takeWhile { it.isWhitespaceOrComment } +
//            leftSiblings.takeWhile { it.isWhitespaceOrComment }
//    toDelete.forEach {
//        it.delete()
//    }
//
//    deleteWithSurroundingComma()
//}

private val PsiElement.isWhitespaceOrComment
    get(): Boolean = this is PsiWhiteSpace || this is PsiComment