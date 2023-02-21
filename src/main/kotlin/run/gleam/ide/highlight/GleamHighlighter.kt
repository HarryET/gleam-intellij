package run.gleam.ide.highlight

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import run.gleam.ide.colors.GleamColor
import run.gleam.lang.core.lexer.GleamLexer
import run.gleam.lang.core.psi.GleamTypes.*

class GleamHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer = GleamLexer()

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> =
        pack(map(tokenType)?.textAttributesKey)

    companion object {
        fun map(tokenType: IElementType?): GleamColor? = when (tokenType) {
            COMMENT_NORMAL -> GleamColor.COMMENT
            COMMENT_DOC -> GleamColor.DOC_COMMENT
            COMMENT_MODULE -> GleamColor.MODULE_COMMENT
            PUB, FN, LET, CASE, IMPORT, TYPE, ASSERT, TODO, CONST, EXTERNAL -> GleamColor.KEYWORD
            STRING -> GleamColor.STRING
            UP_NAME, TYPE_IDENTIFIER -> GleamColor.TYPE_IDENTIFIER
            // TODO implement highlighting for different parts of code
            else -> null
        }
    }
}