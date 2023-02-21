package run.gleam.ide

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiElement
import org.antlr.intellij.adaptor.xpath.XPath
import run.gleam.lang.GleamLanguage

class GleamFoldingBuilder : FoldingBuilderEx() {
    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        val functions = XPath.findAll(GleamLanguage, root, "//function//function_body").map { FoldingDescriptor(it, it.textRange) }
        return (functions).toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode): String {
        return node.firstChildNode.text + "..." + node.lastChildNode.text
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean {
        return false
    }
}