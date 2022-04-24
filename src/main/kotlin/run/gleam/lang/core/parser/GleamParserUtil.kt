package run.gleam.lang.core.parser

import com.intellij.lang.parser.GeneratedParserUtilBase
import com.intellij.lang.*
import com.intellij.psi.tree.*
import run.gleam.lang.core.psi.GleamTypes.*

object GleamParserUtil : GeneratedParserUtilBase() {

    @JvmStatic
    fun parseString(builder: PsiBuilder, level: Int): Boolean = parseLineTokens(builder, setOf(QUOTE))

    @JvmStatic
    fun parseComment(builder: PsiBuilder, level: Int): Boolean = parseLineTokens(builder, setOf(EOL))

    @JvmStatic
    fun parseIdentifier(builder: PsiBuilder, level: Int): Boolean = parseLineTokens(builder, setOf(QUOTE))

    private fun parseLineTokens(builder: PsiBuilder, tokens: Set<IElementType>): Boolean {
        // accept everything till the end of line
        var hasAny = false
        do {
            if (builder.tokenType in tokens || builder.tokenType == null) return hasAny
            builder.advanceLexer()
            hasAny = true
        } while (true)
    }
}