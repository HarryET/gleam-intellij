package run.gleam.lang.core.lexer

import com.github.michaelbull.result.*
import com.intellij.lexer.LexerBase
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.tree.IElementType

class GlexerAdapter : LexerBase() {
    private val logger = Logger.getInstance(
        GlexerAdapter::class.java
    )
    private lateinit var iterator: Iterator<Result<Spanned, LexicalError>>

    private var state: Int = 0
    private var currentTokenStart: Int = 0
    private var currentTokenEnd: Int = 0
    private var tokenType: IElementType? = null
    private lateinit var bufferSequence: CharSequence
    private var bufferEnd: Int = 0
    private var startingOffset: Int = 0

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        val source = buffer.subSequence(startOffset, endOffset).toString()
        iterator = makeTokenizer(source)

        // Initialization logic, if needed
        bufferSequence = buffer
        currentTokenStart = startOffset
        currentTokenEnd = startOffset

        bufferEnd = endOffset
        state = initialState
        startingOffset = startOffset

        advance()
    }

    override fun advance() {
        if (iterator.hasNext() && currentTokenStart <= bufferEnd) {
            val result = iterator.next()

            result.onSuccess {
                currentTokenStart = it.start + startingOffset
                currentTokenEnd = it.end + startingOffset
                tokenType = it.token
            }

            result.onFailure {
                logger.info("${it.error}, ${it.location}")
                tokenType = null
                currentTokenStart = it.location.start
                currentTokenEnd = it.location.end
            }
        } else {
            currentTokenStart = bufferEnd
            currentTokenEnd = bufferEnd
            tokenType = null
        }
    }

    override fun getBufferSequence(): CharSequence {
        return bufferSequence
    }

    override fun getBufferEnd(): Int {
        return bufferEnd
    }

    override fun getState(): Int {
        // Return the current state if your lexer has states, otherwise return 0
        return 0
    }

    override fun getTokenType(): IElementType? {
        return tokenType
    }

    override fun getTokenStart(): Int {
        return currentTokenStart
    }

    override fun getTokenEnd(): Int {
        return currentTokenEnd
    }
}