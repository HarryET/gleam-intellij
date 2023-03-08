package run.gleam.lang.core.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import run.gleam.lang.core.lexer.GleamLexer
import run.gleam.lang.core.psi.GleamFile
import run.gleam.lang.core.psi.GleamTypes
import run.gleam.lang.core.stubs.GleamFileStub

class GleamParserDefinition : ParserDefinition {
    private val whiteSpace = TokenSet.create(TokenType.WHITE_SPACE)
    private val comments = TokenSet.create(GleamTypes.COMMENT_DOC, GleamTypes.COMMENT_MODULE, GleamTypes.COMMENT_NORMAL)

    override fun createLexer(project: Project?): Lexer = GleamLexer()
    override fun createParser(project: Project?): PsiParser = GleamParser()
    override fun createElement(node: ASTNode?): PsiElement = GleamTypes.Factory.createElement(node)
    override fun createFile(viewProvider: FileViewProvider): PsiFile = GleamFile(viewProvider)

    override fun getFileNodeType(): IFileElementType = GleamFileStub.Type
    override fun getCommentTokens(): TokenSet = comments
    override fun getWhitespaceTokens(): TokenSet = whiteSpace
    override fun getStringLiteralElements(): TokenSet = TokenSet.create(GleamTypes.STRING)

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