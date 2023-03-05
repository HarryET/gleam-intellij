package run.gleam.lang.core.psi

import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import run.gleam.lang.GleamLanguage
import run.gleam.lang.core.psi.GleamTypes.*

open class GleamTokenType(debugName: String) : IElementType(debugName, GleamLanguage)

open class GleamElementType(debugName: String) : IElementType(debugName, GleamLanguage)

private fun tokenSetOf(vararg tokens: IElementType) = TokenSet.create(*tokens)

// TokenSets
val GL_GROUPINGS = tokenSetOf(LEFT_PAREN, RIGHT_PAREN, LEFT_SQUARE, RIGHT_SQUARE, LEFT_BRACE, RIGHT_BRACE)

val GL_KEYWORDS = tokenSetOf(
    AS, ASSERT,
    CASE, CONST,
    EXTERNAL,
    FN,
    IF,
    IMPORT,
    LET,
//    OPAQUE,
    PUB,
    TODO,
    TRY,
    TYPE,
    USE
)

val GL_OPERATORS = tokenSetOf(
    PLUS, MINUS, STAR, SLASH, LESS, GREATER, LESS_EQUAL, GREATER_EQUAL, PERCENT,
    PLUS_DOT, MINUS_DOT, STAR_DOT, SLASH_DOT, LESS_DOT, GREATER_DOT, LESS_EQUAL_DOT, GREATER_EQUAL_DOT
)

val GL_FLOAT_OPS = tokenSetOf(PLUS_DOT, MINUS_DOT, STAR_DOT, SLASH_DOT, LESS_DOT, GREATER_DOT, LESS_EQUAL_DOT, GREATER_EQUAL_DOT)

val GL_NORMAL_OPS = tokenSetOf(PLUS, MINUS, STAR, SLASH, LESS, GREATER, LESS_EQUAL, GREATER_EQUAL, PERCENT)

val GL_PUNCTUATION = tokenSetOf(
    COLON, COMMA, HASH, BANG, EQUAL, EQUAL_EQUAL, NOT_EQUAL, VBAR, VBAR_VBAR, AMPER_AMPER,
    LT_LT, GT_GT, PIPE, DOT, R_ARROW, L_ARROW, DOT_DOT
)

val GL_COMMENTS = tokenSetOf(COMMENT_DOC, COMMENT_MODULE, COMMENT_NORMAL)

val GL_BOOLEANS = tokenSetOf(TRUE, FALSE)

val GL_IDENTIFIERS_LITERALS = tokenSetOf(UP_NAME, DOWN_NAME, DISCARD_NAME, INTEGER, FLOAT, STRING)

val GL_STRING_LITERALS = tokenSetOf(STRING)