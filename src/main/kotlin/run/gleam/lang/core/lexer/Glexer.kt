package run.gleam.lang.core.lexer

import com.github.michaelbull.result.*
import com.intellij.psi.tree.IElementType
import run.gleam.lang.core.psi.*
import run.gleam.lang.core.psi.GleamTokens

class Lexer<T : Iterator<Pair<Int, Char>>>(private val chars: T) : Iterator<Result<Spanned, LexicalError>> {
    private val pending: MutableList<Spanned> = mutableListOf()
    private var chr0: Char? = null
    private var chr1: Char? = null
    private var loc0: Int = 0
    private var loc1: Int = 0
    private var location: Int = 0
    private var hasNext: Boolean = true

    init {
        nextChar()
        nextChar()
        location = 0
    }

    private fun innerNext(): Result<Spanned, LexicalError> {
        while (pending.isEmpty()) {
            consumeNormal()
        }

        return Ok(pending.removeAt(0))
    }

    private fun consumeNormal() {
        if (chr0 != null) {
            var checkForMinus = false
            when {
                isUpnameStart(chr0!!) -> {
                    val name = lexUpname()
                    name.component1()?.let { emit(it) }
                }
                isNameStart(chr0!!) -> {
                    checkForMinus = true
                    val name = lexName()
                    name.component1()?.let { emit(it) }
                }
                isNumberStart(chr0!!, chr1) -> {
                    checkForMinus = true
                    val num = lexNumber()
                    num.component1()?.let { emit(it) }
                }
                else -> {
                    consumeCharacter(chr0!!)
                }
            }
            if (checkForMinus) {
                if (chr0 == '-' && isNumberStart('-', chr1)) {
                    eatSingleChar(GleamTokens.MINUS)
                }
            }
        } else {
            val tokPos = getPos()
//            emit(Spanned(tokPos - 1, GleamTokens.END_OF_FILE, tokPos))
            emit(Spanned(tokPos - 1, GleamTokens.WHITESPACE, tokPos))
            hasNext = false
        }
    }

    private fun consumeCharacter(c: Char) {
        when (c) {
            '@' -> {
                val tokStart = getPos()
                nextChar()
                val tokEnd = getPos()
                emit(Spanned(tokStart, GleamTokens.AT, tokEnd))
            }
            '"' -> {
                val string = lexString()
                string.component1()?.let { emit(it) }
            }
            '=' -> {
                val tokStart = getPos()
                nextChar()
                when (chr0) {
                    '=' -> {
                        nextChar()
                        val tokEnd = getPos()
                        emit(Spanned(tokStart, GleamTokens.EQUAL_EQUAL, tokEnd))
                    }
                    else -> {
                        val tokEnd = getPos()
                        emit(Spanned(tokStart, GleamTokens.EQUAL, tokEnd))
                    }
                }
            }
            '+' -> {
                val tokStart = getPos()
                nextChar()
                if (chr0 == '.') {
                    nextChar()
                    val tokEnd = getPos()
                    emit(Spanned(tokStart, GleamTokens.PLUS_DOT, tokEnd))
                } else {
                    val tokEnd = getPos()
                    emit(Spanned(tokStart, GleamTokens.PLUS, tokEnd))
                }
            }
            '*' -> {
                val tokStart = getPos()
                nextChar()
                when (chr0) {
                    '.' -> {
                        nextChar()
                        val tokEnd = getPos()
                        emit(Spanned(tokStart, GleamTokens.STAR_DOT, tokEnd))
                    }
                    else -> {
                        val tokEnd = getPos()
                        emit(Spanned(tokStart, GleamTokens.STAR, tokEnd))
                    }
                }
            }
            '/' -> {
                val tokStart = getPos()
                nextChar()
                when (chr0) {
                    '.' -> {
                        nextChar()
                        val tokEnd = getPos()
                        emit(Spanned(tokStart, GleamTokens.SLASH_DOT, tokEnd))
                    }
                    '/' -> {
                        nextChar()
                        val comment = lexComment(tokStart)
                        emit(comment)
                    }
                    else -> {
                        val tokEnd = getPos()
                        emit(Spanned(tokStart, GleamTokens.SLASH, tokEnd))
                    }
                }
            }
            '%' -> {
                val tokStart = getPos()
                nextChar()
                val tokEnd = getPos()
                emit(Spanned(tokStart, GleamTokens.PERCENT, tokEnd))
            }
            '|' -> {
                val tokStart = getPos()
                nextChar()
                when (chr0) {
                    '|' -> {
                        nextChar()
                        val tokEnd = getPos()
                        emit(Spanned(tokStart, GleamTokens.VBAR_VBAR, tokEnd))
                    }
                    '>' -> {
                        nextChar()
                        val tokEnd = getPos()
                        emit(Spanned(tokStart, GleamTokens.PIPE, tokEnd))
                    }
                    else -> {
                        val tokEnd = getPos()
                        emit(Spanned(tokStart, GleamTokens.VBAR, tokEnd))
                    }
                }
            }
            '&' -> {
                val tokStart = getPos()
                nextChar()
                if (chr0 == '&') {
                    nextChar()
                    val tokEnd = getPos()
                    emit(Spanned(tokStart, GleamTokens.AMPER_AMPER, tokEnd))
                } else {
                    Err(LexicalError(LexicalErrorType.UnrecognizedToken('&'), SrcSpan(tokStart, tokStart)))
                }
            }
            '-' -> {
                val tokStart = getPos()
                nextChar()
                when (chr0) {
                    '.' -> {
                        nextChar()
                        val tokEnd = getPos()
                        emit(Spanned(tokStart, GleamTokens.MINUS_DOT, tokEnd))
                    }
                    '>' -> {
                        nextChar()
                        val tokEnd = getPos()
                        emit(Spanned(tokStart, GleamTokens.R_ARROW, tokEnd))
                    }
                    else -> {
                        val tokEnd = getPos()
                        emit(Spanned(tokStart, GleamTokens.MINUS, tokEnd))
                    }
                }
            }
            '!' -> {
                val tokStart = getPos()
                nextChar()
                if (chr0 == '=') {
                    nextChar()
                    val tokEnd = getPos()
                    emit(Spanned(tokStart, GleamTokens.NOT_EQUAL, tokEnd))
                } else {
                    val tokEnd = getPos()
                    emit(Spanned(tokStart, GleamTokens.BANG, tokEnd))
                }
            }
            '(' -> {
                eatSingleChar(GleamTokens.LEFT_PAREN)
            }
            ')' -> {
                eatSingleChar(GleamTokens.RIGHT_PAREN)
            }
            '[' -> {
                eatSingleChar(GleamTokens.LEFT_SQUARE)
            }
            ']' -> {
                eatSingleChar(GleamTokens.RIGHT_SQUARE)
            }
            '{' -> {
                eatSingleChar(GleamTokens.LEFT_BRACE)
            }
            '}' -> {
                eatSingleChar(GleamTokens.RIGHT_BRACE)
            }
            ':' -> {
                eatSingleChar(GleamTokens.COLON)
            }
            '<' -> {
                val tokStart = getPos()
                nextChar()
                when (chr0) {
                    '>' -> {
                        nextChar()
                        val tokEnd = getPos()
                        emit(Spanned(tokStart, GleamTokens.LT_GT, tokEnd))
                    }
                    '<' -> {
                        nextChar()
                        val tokEnd = getPos()
                        emit(Spanned(tokStart, GleamTokens.LT_LT, tokEnd))
                    }
                    '.' -> {
                        nextChar()
                        val tokEnd = getPos()
                        emit(Spanned(tokStart, GleamTokens.LESS_DOT, tokEnd))
                    }
                    '-' -> {
                        nextChar()
                        val tokEnd = getPos()
                        emit(Spanned(tokStart, GleamTokens.L_ARROW, tokEnd))
                    }
                    '=' -> {
                        nextChar()
                        when {
                            chr0 == '.' -> {
                                nextChar()
                                val tokEnd = getPos()
                                emit(Spanned(tokStart, GleamTokens.LESS_EQUAL_DOT, tokEnd))
                            }

                            else -> {
                                val tokEnd = getPos()
                                emit(Spanned(tokStart, GleamTokens.LESS_EQUAL, tokEnd))
                            }
                        }
                    }
                    else -> {
                        val tokEnd = getPos()
                        emit(Spanned(tokStart, GleamTokens.LESS, tokEnd))
                    }
                }
            }
            '>' -> {
                val tokStart = getPos()
                nextChar()
                when (chr0) {
                    '>' -> {
                        nextChar()
                        val tokEnd = getPos()
                        emit(Spanned(tokStart, GleamTokens.GT_GT, tokEnd))
                    }
                    '.' -> {
                        nextChar()
                        val tokEnd = getPos()
                        emit(Spanned(tokStart, GleamTokens.GREATER_DOT, tokEnd))
                    }
                    '=' -> {
                        nextChar()
                        when {
                            chr0 == '.' -> {
                                nextChar()
                                val tokEnd = getPos()
                                emit(Spanned(tokStart, GleamTokens.GREATER_EQUAL_DOT, tokEnd))
                            }

                            else -> {
                                val tokEnd = getPos()
                                emit(Spanned(tokStart, GleamTokens.GREATER_EQUAL, tokEnd))
                            }
                        }
                    }
                    else -> {
                        val tokEnd = getPos()
                        emit(Spanned(tokStart, GleamTokens.GREATER, tokEnd))
                    }
                }
            }
            ',' -> {
                eatSingleChar(GleamTokens.COMMA)
            }
            '.' -> {
                val tokStart = getPos()
                nextChar()
                if (chr0 == '.') {
                    nextChar()
                    val tokEnd = getPos()
                    emit(Spanned(tokStart, GleamTokens.DOT_DOT, tokEnd))
                } else {
                    val tokEnd = getPos()
                    emit(Spanned(tokStart, GleamTokens.DOT, tokEnd))
                }
            }
            '#' -> {
                eatSingleChar(GleamTokens.HASH)
            }
            '\n' -> {
                nextChar()
                val tokStart = getPos()
                while (chr0 != null) {
                    when (chr0) {
                        ' ', '\t', '\u000C' -> {
                            nextChar()
                        }
                        '\n' -> {
                            val tokEnd = getPos()
                            emit(Spanned(tokStart - 1, GleamTokens.EMPTY_LINE, tokEnd))
                            break
                        }
                        else -> {
                            val tokEnd = getPos()
                            emit(Spanned(tokStart - 1, GleamTokens.WHITESPACE, tokEnd))
                            break
                        }
                    }
                }
            }
            ' ', '\t', '\u000C' -> {
                val tokStart = getPos()
                nextChar()
                while (chr0 != null) {
                    when (chr0) {
                        ' ', '\t', '\u000C' -> {
                            nextChar()
                        }
                        else -> {
                            val tokEnd = getPos()
                            emit(Spanned(tokStart, GleamTokens.WHITESPACE, tokEnd))
                            break
                        }
                    }
                }
            }
            else -> {
                val location = getPos()
                Err(LexicalError(LexicalErrorType.UnrecognizedToken(c), SrcSpan(location, location)))
            }
        }
    }

    private fun lexName(): Result<Spanned, LexicalError> {
        val name = StringBuilder()
        val startPos = getPos()

        while (isNameContinuation()) {
            name.append(nextChar()!!)
        }

        if (isNameErrorContinuation()) {
            while (isNameErrorContinuation()) {
                name.append(nextChar()!!)
            }
            val endPos = getPos()
            if (name.startsWith('_')) {
                Err(LexicalError(LexicalErrorType.BadDiscardName(name.toString()), SrcSpan(startPos, endPos)))
            } else {
                Err(LexicalError(LexicalErrorType.BadName(name.toString()), SrcSpan(startPos, endPos)))
            }
        }

        val endPos = getPos()

        val token = strToKeyword(name.toString())
            ?: if (name.startsWith('_')) withValue(GleamTokens.DISCARD_NAME, name.toString()) else withValue(GleamTokens.NAME, name.toString())

        return Ok(Spanned(startPos, token, endPos))
    }

    private fun lexUpname(): Result<Spanned, LexicalError> {
        val name = StringBuilder()
        val startPos = getPos()

        while (isUpnameContinuation()) {
            name.append(nextChar()!!)
        }

        val endPos = getPos()

        val token = strToKeyword(name.toString()) ?: withValue(GleamTokens.UP_NAME, name.toString())

        return Ok(Spanned(startPos, token, endPos))
    }

    private fun lexNumber(): Result<Spanned, LexicalError> {
        val startPos = getPos()
        val num = if (chr0 == '0') {
            when (chr1) {
                'x', 'X' -> {
                    nextChar()
                    nextChar()
                    lexNumberRadix(startPos, 16, "0x")
                }
                'o', 'O' -> {
                    nextChar()
                    nextChar()
                    lexNumberRadix(startPos, 8, "0o")
                }
                'b', 'B' -> {
                    nextChar()
                    nextChar()
                    lexNumberRadix(startPos, 2, "0b")
                }
                else -> lexDecimalNumber()
            }
        } else {
            lexDecimalNumber()
        }

        if (chr0 == '_') {
            val location = getPos()
            Err(LexicalError(LexicalErrorType.NumTrailingUnderscore, SrcSpan(location, location)))
        }

        return num
    }

    private fun lexNumberRadix(startPos: Int, radix: Int, prefix: String): Result<Spanned, LexicalError> {
        val num = radixRun(radix)
        return if (num.isEmpty()) {
            val location = getPos() - 1
            Err(LexicalError(LexicalErrorType.RadixIntNoValue, SrcSpan(location, location)))
        } else if (radix < 16 && isDigitOfRadix(chr0, 16)) {
            val location = getPos()
            Err(LexicalError(LexicalErrorType.DigitOutOfRadix, SrcSpan(location, location)))
        } else {
            val value = "$prefix$num"
            val endPos = getPos()
            Ok(Spanned(startPos, withValue(GleamTokens.INT, value), endPos))
        }
    }

    private fun lexDecimalNumber(): Result<Spanned, LexicalError> {
        val startPos = getPos()
        val value = StringBuilder()

        if (chr0 == '-') {
            value.append(nextChar()!!)
        }

        value.append(radixRun(10))

        if (chr0 == '.') {
            value.append(nextChar()!!)
            value.append(radixRun(10))

            if (chr0 == 'e') {
                value.append(nextChar()!!)
                if (chr0 == '-') {
                    value.append(nextChar()!!)
                }
                value.append(radixRun(10))
            }

            val endPos = getPos()
            return Ok(Spanned(startPos, withValue(GleamTokens.FLOAT, value), endPos))
        }

        val endPos = getPos()
        return Ok(Spanned(startPos, withValue(GleamTokens.INT, value), endPos))
    }

    private fun radixRun(radix: Int): String {
        val valueText = StringBuilder()

        while (takeNumber(radix) != null || (chr0 == '_' && isDigitOfRadix(chr1, radix))) {
            if (chr0 != null) {
                valueText.append(chr0!!)
            }
            if (chr0 == '_') {
                nextChar()
            }
        }

        return valueText.toString()
    }

    private fun takeNumber(radix: Int): Char? {
        val takeChar = isDigitOfRadix(chr0, radix)

        return if (takeChar) {
            nextChar()
        } else {
            null
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun isDigitOfRadix(c: Char?, radix: Int): Boolean {
        return when (radix) {
            2, 8, 10, 16 -> c?.digitToIntOrNull(radix) !== null
            else -> throw IllegalArgumentException("Radix not implemented: $radix")
        }
    }

    private fun lexString(): Result<Spanned, LexicalError> {
        val startPos = getPos()
        nextChar() // advance past the first quote
        val stringContent = StringBuilder()

        loop@ while (true) {
            when (val c = nextChar()) {
                '\\' -> {
                    val slashPos = getPos() - 1
                    when (val nextChar = chr0) {
                        'e', 'f', 'n', 'r', 't', '"', '\\' -> {
                            nextChar()
                            stringContent.append('\\')
                            stringContent.append(nextChar)
                        }
                        else -> {
                            Err(LexicalError(LexicalErrorType.BadStringEscape, SrcSpan(slashPos, slashPos + 1)))
                        }
                    }
                }
                '"' -> break@loop
                null -> Err(LexicalError(LexicalErrorType.UnexpectedStringEnd, SrcSpan(startPos, startPos)))
                else -> stringContent.append(c)
            }
        }

        val endPos = getPos()

        return Ok(Spanned(startPos, withValue(GleamTokens.STRING, stringContent.toString()), endPos))
    }

    enum class CommentKind {
        Comment,
        Doc,
        ModuleDoc,
    }

    private fun lexComment(tokStart: Int): Spanned {
        val kind = when {
            Pair(this.chr0, this.chr1) == Pair('/', '/') -> {
                this.nextChar()
                this.nextChar()
                CommentKind.ModuleDoc
            }
            Pair(this.chr0, this.chr1).first == '/' -> {
                this.nextChar()
                CommentKind.Doc
            }
            else -> CommentKind.Comment
        }

        val content = StringBuilder()
        while ('\n' != this.chr0) {
            when (this.chr0) {
                null -> break
                else -> content.append(this.chr0)
            }
            this.nextChar()
        }
        val endPos = this.getPos()
        val token = when (kind) {
            CommentKind.Comment -> GleamTokens.COMMENT_NORMAL
            CommentKind.Doc -> withValue(GleamTokens.COMMENT_DOC, content.toString())
            CommentKind.ModuleDoc -> GleamTokens.COMMENT_MODULE
        }
        return Spanned(tokStart, token, endPos)
    }

    private fun isNameStart(c: Char): Boolean {
        return c == '_' || c in 'a'..'z'
    }

    private fun isUpnameStart(c: Char): Boolean {
        return c.isUpperCase()
    }

    private fun isNumberStart(c: Char, c1: Char?): Boolean {
        return when (c) {
            in '0'..'9' -> true
            '-' -> c1 in '0'..'9'
            else -> false
        }
    }

    private fun isNameContinuation(): Boolean {
        return chr0?.let { it == '_' || it.isDigit() || it in 'a'..'z' } ?: false
    }

    private fun isUpnameContinuation(): Boolean {
        return chr0?.let { it.isDigit() || it in 'a'..'z' || it in 'A'..'Z' } ?: false
    }

    private fun isNameErrorContinuation(): Boolean {
        return chr0?.let { it == '_' || it.isDigit() || it in 'a'..'z' || it in 'A'..'Z' } ?: false
    }

    private fun eatSingleChar(ty: IElementType) {
        val tokStart = getPos()
        nextChar()
        val tokEnd = getPos()
        emit(Spanned(tokStart, ty, tokEnd))
    }

    private fun nextChar(): Char? {
        val c = chr0
        val next: Pair<Int, Char>
        val nxt: Char?

        if (chars.hasNext()) {
            next = chars.next()
            loc0 = loc1
            loc1 = next.first
            nxt = next.second
        } else {
            // EOF needs a single advance
            loc0 = loc1
            loc1 += 1
            nxt = null
        }
        chr0 = chr1
        chr1 = nxt
        return c
    }

    private fun getPos(): Int {
        return loc0
    }

    private fun emit(spanned: Spanned) {
        pending.add(spanned)
    }

    override fun hasNext(): Boolean {
        return hasNext
    }

    override fun next(): Result<Spanned, LexicalError> {
        val token = innerNext()

        if (token.component1()?.token == GleamTokens.END_OF_FILE) {
            hasNext = false
        }

        return token
    }
}

fun makeTokenizer(source: String): Iterator<Result<Spanned, LexicalError>> {
    val chars = source.withIndex().map { it.index to it.value }
    val nlh = NewlineHandler(chars.iterator())
    return Lexer(nlh)
}

fun strToKeyword(word: String): IElementType? {
    return when (word) {
        "as" -> GleamTokens.AS
        "assert" -> GleamTokens.ASSERT
        "case" -> GleamTokens.CASE
        "const" -> GleamTokens.CONST
        "fn" -> GleamTokens.FN
        "if" -> GleamTokens.IF
        "import" -> GleamTokens.IMPORT
        "let" -> GleamTokens.LET
        "opaque" -> GleamTokens.OPAQUE
        "panic" -> GleamTokens.PANIC
        "pub" -> GleamTokens.PUB
        "todo" -> GleamTokens.TODO
        "type" -> GleamTokens.TYPE
        "use" -> GleamTokens.USE
        else -> null
    }
}

class NewlineHandler(private val source: Iterator<Pair<Int, Char>>) : Iterator<Pair<Int, Char>> {

    private var chr0: Pair<Int, Char>? = null
    private var chr1: Pair<Int, Char>? = null

    init {
        shift()
        shift()
    }

    private fun shift(): Pair<Int, Char>? {
        val result = chr0
        chr0 = chr1
        chr1 = source.nextOrNull()
        return result
    }

    override fun hasNext(): Boolean {
        return chr0 != null
    }

    override fun next(): Pair<Int, Char> {
        // Collapse \r\n into \n
        while (chr0 != null && chr0!!.second == '\r') {
            if (chr1 != null && chr1!!.second == '\n') {
                // Transform Windows EOL into \n
                shift()
            } else {
                // Transform MacOS EOL into \n
                chr0 = Pair(chr0!!.first, '\n')
            }
        }

        return shift()!!
    }

    private fun <T> Iterator<T>.nextOrNull(): T? {
        return if (hasNext()) next() else null
    }
}

data class Spanned(val start: Int, val token: IElementType, val end: Int)

data class LexicalError(val error: LexicalErrorType, val location: SrcSpan)

sealed class LexicalErrorType {
    data class UnrecognizedToken(val tok: Char) : LexicalErrorType()
    data class BadDiscardName(val name: String) : LexicalErrorType()
    data class BadName(val name: String) : LexicalErrorType()
    data object NumTrailingUnderscore : LexicalErrorType()
    data object RadixIntNoValue : LexicalErrorType()
    data object DigitOutOfRadix : LexicalErrorType()
    data object BadStringEscape : LexicalErrorType()
    data object UnexpectedStringEnd : LexicalErrorType()
}

data class SrcSpan(
    val start: Int,
    val end: Int
) {
    @OptIn(ExperimentalStdlibApi::class)
    fun contains(byteIndex: Int): Boolean {
        return byteIndex in start..<end
    }
}