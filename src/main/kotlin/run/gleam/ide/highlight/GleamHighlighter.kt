package run.gleam.ide.highlight

import GleamLexer
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import org.antlr.intellij.adaptor.lexer.ANTLRLexerAdaptor
import org.antlr.intellij.adaptor.lexer.TokenIElementType
import run.gleam.ide.colors.GleamColor
import run.gleam.lang.GleamLanguage

class GleamHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer = ANTLRLexerAdaptor(GleamLanguage, GleamLexer(null))

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> =
        pack(map(tokenType)?.textAttributesKey)

    companion object {
        fun map(tokenType: IElementType?): GleamColor? {
            if (tokenType !is TokenIElementType) return null;
            val ttype = tokenType.antlrTokenType;
            return when (ttype) {
                GleamLexer.COMMENT_NORMAL -> GleamColor.COMMENT
                GleamLexer.COMMENT_DOC -> GleamColor.DOC_COMMENT
                GleamLexer.COMMENT_MODULE -> GleamColor.MODULE_COMMENT

                GleamLexer.AS, GleamLexer.ASSERT, GleamLexer.CASE, GleamLexer.CONST,
                GleamLexer.EXTERNAL, GleamLexer.FN, GleamLexer.IF, GleamLexer.IMPORT,
                GleamLexer.LET, GleamLexer.OPAQUE, GleamLexer.PUB, GleamLexer.TODO,
                GleamLexer.TRY, GleamLexer.TYPE, GleamLexer.USE -> GleamColor.KEYWORD

                GleamLexer.STRING -> GleamColor.STRING
                GleamLexer.DECIMAL -> GleamColor.NUMBER
                GleamLexer.TRUE, GleamLexer.FALSE -> GleamColor.BOOLEAN

                GleamLexer.LEFT_BRACE, GleamLexer.RIGHT_BRACE -> GleamColor.BRACES
                GleamLexer.LEFT_PAREN, GleamLexer.RIGHT_PAREN -> GleamColor.PARENTHESES

                GleamLexer.EQUAL, GleamLexer.EQUAL_EQUAL, GleamLexer.NOT_EQUAL,
                GleamLexer.GREATER, GleamLexer.GREATER_EQUAL, GleamLexer.GREATER_EQUAL_DOT,
                GleamLexer.GREATER_DOT, GleamLexer.LESS, GleamLexer.LESS_EQUAL,
                GleamLexer.LESS_EQUAL_DOT, GleamLexer.LESS_DOT -> GleamColor.OPERATION_SIGN

                else -> null
            }
        }
    }
}