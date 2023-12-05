package run.gleam.lang.core.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

public class GleamTokens {
    // Identifiers
    public static final IElementType NAME = new GleamTokenTypeJava("NAME");
    public static final IElementType BAD_NAME = new GleamTokenTypeJava("BAD_NAME");
    public static final IElementType UP_NAME = new GleamTokenTypeJava("UP_NAME");
    public static final IElementType BAD_UP_NAME = new GleamTokenTypeJava("BAD_UP_NAME");
    public static final IElementType DISCARD_NAME = new GleamTokenTypeJava("DISCARD_NAME");
    public static final IElementType BAD_DISCARD_NAME = new GleamTokenTypeJava("BAD_DISCARD_NAME");
    // Literals
    public static final IElementType INT = new GleamTokenTypeJava("INT_LITERAL");
    public static final IElementType FLOAT = new GleamTokenTypeJava("FLOAT_LITERAL");
    public static final IElementType STRING = new GleamTokenTypeJava("STRING_LITERAL");
    public static final IElementType UNEXPECTED_STRING_END = new GleamTokenTypeJava("UNEXPECTED_STRING_END");
    public static final IElementType NUM_TRAILING_UNDERSCORE = new GleamTokenTypeJava("NUM_TRAILING_UNDERSCORE");
    public static final IElementType RADIX_INT_NO_VALUE = new GleamTokenTypeJava("RADIX_INT_NO_VALUE");
    public static final IElementType RADIX_INT_DIGIT_OUT_OF_RADIX = new GleamTokenTypeJava("RADIX_INT_DIGIT_OUT_OF_RADIX");
    // Groupings
    public static final IElementType LEFT_PAREN = new GleamTokenTypeJava("LeftParen");
    public static final IElementType RIGHT_PAREN = new GleamTokenTypeJava("RightParen");
    public static final IElementType LEFT_SQUARE = new GleamTokenTypeJava("LeftSquare");
    public static final IElementType RIGHT_SQUARE = new GleamTokenTypeJava("RightSquare");
    public static final IElementType LEFT_BRACE = new GleamTokenTypeJava("LeftBrace");
    public static final IElementType RIGHT_BRACE = new GleamTokenTypeJava("RightBrace");
    // Operators
    // Int
    public static final IElementType PLUS = new GleamTokenTypeJava("Plus");
    public static final IElementType MINUS = new GleamTokenTypeJava("Minus");
    public static final IElementType STAR = new GleamTokenTypeJava("Star");
    public static final IElementType SLASH = new GleamTokenTypeJava("Slash");
    public static final IElementType LESS = new GleamTokenTypeJava("Less");
    public static final IElementType GREATER = new GleamTokenTypeJava("Greater");
    public static final IElementType LESS_EQUAL = new GleamTokenTypeJava("LessEqual");
    public static final IElementType GREATER_EQUAL = new GleamTokenTypeJava("GreaterEqual");
    public static final IElementType PERCENT = new GleamTokenTypeJava("Percent");
    // Float
    public static final IElementType PLUS_DOT = new GleamTokenTypeJava("PlusDot");
    public static final IElementType MINUS_DOT = new GleamTokenTypeJava("MinusDot");
    public static final IElementType STAR_DOT = new GleamTokenTypeJava("StarDot");
    public static final IElementType SLASH_DOT = new GleamTokenTypeJava("SlashDot");
    public static final IElementType LESS_DOT = new GleamTokenTypeJava("LessDot");
    public static final IElementType GREATER_DOT = new GleamTokenTypeJava("GreaterDot");
    public static final IElementType LESS_EQUAL_DOT = new GleamTokenTypeJava("LessEqualDot");
    public static final IElementType GREATER_EQUAL_DOT = new GleamTokenTypeJava("GreaterEqualDot");
    // String
    public static final IElementType LT_GT = new GleamTokenTypeJava("LtGt");
    // Other Punctuation
    public static final IElementType COLON = new GleamTokenTypeJava("Colon");
    public static final IElementType COMMA = new GleamTokenTypeJava("Comma");
    public static final IElementType HASH = new GleamTokenTypeJava("Hash");
    public static final IElementType BANG = new GleamTokenTypeJava("Bang");
    public static final IElementType EQUAL = new GleamTokenTypeJava("Equal");
    public static final IElementType EQUAL_EQUAL = new GleamTokenTypeJava("EqualEqual");
    public static final IElementType NOT_EQUAL = new GleamTokenTypeJava("NotEqual");
    public static final IElementType VBAR = new GleamTokenTypeJava("Vbar");
    public static final IElementType VBAR_VBAR = new GleamTokenTypeJava("VbarVbar");
    public static final IElementType AMPER_AMPER = new GleamTokenTypeJava("AmperAmper");
    public static final IElementType LT_LT = new GleamTokenTypeJava("LtLt");
    public static final IElementType GT_GT = new GleamTokenTypeJava("GtGt");
    public static final IElementType PIPE = new GleamTokenTypeJava("Pipe");
    public static final IElementType DOT = new GleamTokenTypeJava("Dot");
    public static final IElementType R_ARROW = new GleamTokenTypeJava("RArrow");
    public static final IElementType L_ARROW = new GleamTokenTypeJava("LArrow");
    public static final IElementType DOT_DOT = new GleamTokenTypeJava("DotDot");
    public static final IElementType END_OF_FILE = new GleamTokenTypeJava("EndOfFile");
    // Extra
    public static final IElementType COMMENT_NORMAL = new GleamTokenTypeJava("CommentNormal");
    public static final IElementType COMMENT_DOC = new GleamTokenTypeJava("CommentDoc");
    public static final IElementType COMMENT_MODULE = new GleamTokenTypeJava("CommentModule");
    public static final IElementType EMPTY_LINE = new GleamTokenTypeJava("EmptyLine");
    public static final IElementType WHITESPACE = new GleamTokenTypeJava("WHITESPACE");
    // Keywords
    public static final IElementType AS = new GleamTokenTypeJava("As");
    public static final IElementType AT = new GleamTokenTypeJava("At");
    public static final IElementType ASSERT = new GleamTokenTypeJava("Assert");
    public static final IElementType CASE = new GleamTokenTypeJava("Case");
    public static final IElementType CONST = new GleamTokenTypeJava("Const");
    public static final IElementType EXTERNAL = new GleamTokenTypeJava("External");
    public static final IElementType FN = new GleamTokenTypeJava("Fn");
    public static final IElementType IF = new GleamTokenTypeJava("If");
    public static final IElementType IMPORT = new GleamTokenTypeJava("Import");
    public static final IElementType LET = new GleamTokenTypeJava("Let");
    public static final IElementType OPAQUE = new GleamTokenTypeJava("Opaque");
    public static final IElementType PUB = new GleamTokenTypeJava("Pub");
    public static final IElementType TODO = new GleamTokenTypeJava("Todo");
    public static final IElementType TRY = new GleamTokenTypeJava("Try");
    public static final IElementType TYPE = new GleamTokenTypeJava("Type");
    public static final IElementType USE = new GleamTokenTypeJava("Use");
    public static final IElementType PANIC = new GleamTokenTypeJava("Panic");
    // Unofficial Tokens
    public static final IElementType TRUE = new GleamTokenTypeJava("True");
    public static final IElementType FALSE = new GleamTokenTypeJava("False");

    private static TokenSet tokenSetOf(IElementType... tokens) {
        return TokenSet.create(tokens);
    }

    // TokenSets
    public static final TokenSet WHITESPACES = tokenSetOf(WHITESPACE, EMPTY_LINE);
    public static final TokenSet GL_GROUPINGS = tokenSetOf(LEFT_PAREN, RIGHT_PAREN, LEFT_SQUARE, RIGHT_SQUARE, LEFT_BRACE, RIGHT_BRACE);
    public static final TokenSet GL_KEYWORDS = tokenSetOf(
            AS, ASSERT,
            CASE, CONST,
            EXTERNAL,
            FN,
            IF,
            IMPORT,
            LET,
            OPAQUE,
            PUB,
            TODO,
            TRY,
            TYPE,
            USE
    );
    public static final TokenSet GL_OPERATORS = tokenSetOf(
            PLUS, MINUS, STAR, SLASH, LESS, GREATER, LESS_EQUAL, GREATER_EQUAL, PERCENT,
            PLUS_DOT, MINUS_DOT, STAR_DOT, SLASH_DOT, LESS_DOT, GREATER_DOT, LESS_EQUAL_DOT, GREATER_EQUAL_DOT
    );
    public static final TokenSet GL_FLOAT_OPS =
            tokenSetOf(PLUS_DOT, MINUS_DOT, STAR_DOT, SLASH_DOT, LESS_DOT, GREATER_DOT, LESS_EQUAL_DOT, GREATER_EQUAL_DOT);
    public static final TokenSet GL_NORMAL_OPS = tokenSetOf(PLUS, MINUS, STAR, SLASH, LESS, GREATER, LESS_EQUAL, GREATER_EQUAL, PERCENT);
    public static final TokenSet GL_PUNCTUATION = tokenSetOf(
            COLON, COMMA, HASH, BANG, EQUAL, EQUAL_EQUAL, NOT_EQUAL, VBAR, VBAR_VBAR, AMPER_AMPER,
            LT_LT, GT_GT, PIPE, DOT, R_ARROW, L_ARROW, DOT_DOT, END_OF_FILE
    );
    public static final TokenSet GL_COMMENTS = tokenSetOf(COMMENT_DOC, COMMENT_MODULE, COMMENT_NORMAL);
    public static final TokenSet GL_BOOLEANS = tokenSetOf(TRUE, FALSE);
    public static final TokenSet GL_IDENTIFIERS_LITERALS = tokenSetOf(NAME, UP_NAME, BAD_UP_NAME, DISCARD_NAME, INT, FLOAT, STRING);
    public static final TokenSet GL_STRING_LITERALS = tokenSetOf(STRING);
}
