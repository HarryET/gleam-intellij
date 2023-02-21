package run.gleam.lang.core.lexer

import com.intellij.lexer.FlexAdapter
import run.gleam.lang.core.parser._GleamLexer


class GleamLexer : FlexAdapter(_GleamLexer())
