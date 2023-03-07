package run.gleam.lang.core.psi.ext

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.UserDataHolderEx
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.tree.CompositePsiElement
import com.intellij.psi.search.SearchScope
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.tree.IElementType
import com.intellij.util.Query
import run.gleam.lang.core.psi.GleamTypes.COMMA

interface GleamElement : PsiElement, UserDataHolderEx {}
abstract class GleamElementImpl(type: IElementType): CompositePsiElement(type), GleamElement {
    override fun toString(): String = "${javaClass.simpleName}($elementType)"
}

abstract class GleamStubbedElementImpl<StubT : StubElement<*>> : StubBasedPsiElementBase<StubT>, GleamElement {
    constructor(node: ASTNode) : super(node)
    constructor(stub: StubT, nodeType: IStubElementType<*, *>) : super(stub, nodeType)
    override fun toString(): String = "${javaClass.simpleName}($elementType)"
}

/**
 * Delete the element along with a neighbour comma.
 * If a comma follows the element, it will be deleted.
 * Else if a comma precedes the element, it will be deleted.
 *
 * It is useful to remove elements that are parts of comma separated lists (parameters, arguments, use specks, ...).
 */
fun GleamElement.deleteWithSurroundingComma() {
    val followingComma = getNextNonCommentSibling()
    if (followingComma?.elementType == COMMA) {
        followingComma?.delete()
    } else {
        val precedingComma = getPrevNonCommentSibling()
        if (precedingComma?.elementType == COMMA) {
            precedingComma?.delete()
        }
    }

    delete()
}

/**
 * Delete the element along with all surrounding whitespace and a single surrounding comma.
 * See [deleteWithSurroundingComma].
 */
fun GleamElement.deleteWithSurroundingCommaAndWhitespace() {
    val toDelete = rightSiblings.takeWhile { it.isWhitespaceOrComment } +
            leftSiblings.takeWhile { it.isWhitespaceOrComment }
    toDelete.forEach {
        it.delete()
    }

    deleteWithSurroundingComma()
}

private val PsiElement.isWhitespaceOrComment
    get(): Boolean = this is PsiWhiteSpace || this is PsiComment

fun GleamElement.searchReferences(scope: SearchScope? = null): Query<PsiReference> {
    return if (scope == null) {
        ReferencesSearch.search(this)
    } else {
        ReferencesSearch.search(this, scope)
    }
}