package run.gleam.lang.core.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import run.gleam.lang.GleamLanguage
import run.gleam.lang.core.lexer.GlexerAdapter
import run.gleam.lang.core.psi.GleamElementTypes
import run.gleam.lang.core.psi.GleamFile
import run.gleam.lang.core.psi.GleamTokens

class GleamParserDefinition : ParserDefinition {
    override fun createLexer(project: Project?): Lexer = GlexerAdapter()
    override fun createParser(project: Project?): PsiParser = GleamParser()
    override fun createElement(node: ASTNode?): PsiElement = GleamElementTypes.Factory.createElement(node)
    override fun createFile(viewProvider: FileViewProvider): PsiFile = GleamFile(viewProvider)
    private val file = IFileElementType(GleamLanguage)
    override fun getFileNodeType(): IFileElementType = file
    override fun getCommentTokens(): TokenSet = GleamTokens.GL_COMMENTS
    override fun getWhitespaceTokens(): TokenSet = GleamTokens.WHITESPACES
    override fun getStringLiteralElements(): TokenSet = TokenSet.create(GleamTokens.STRING)

    companion object {
        /**
         * Should be increased after any change of lexer rules
         */
        const val LEXER_VERSION: Int = 1

        /**
         * Should be increased after any change of parser rules
         */
        const val PARSER_VERSION: Int = LEXER_VERSION + 1
    }
}