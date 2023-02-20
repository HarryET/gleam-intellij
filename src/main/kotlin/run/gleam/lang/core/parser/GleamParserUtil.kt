package run.gleam.lang.core.parser

import com.intellij.lang.parser.GeneratedParserUtilBase
import com.intellij.lang.*
import com.intellij.psi.tree.*

object GleamParserUtil : GeneratedParserUtilBase() {
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