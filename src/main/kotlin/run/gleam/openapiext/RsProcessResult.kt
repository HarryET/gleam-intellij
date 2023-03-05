/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package run.gleam.openapiext

import com.fasterxml.jackson.core.JacksonException
import com.intellij.execution.ExecutionException
import com.intellij.execution.process.ProcessOutput
import run.gleam.stdext.RsResult

typealias GlProcessResult<T> = RsResult<T, GlProcessExecutionException>

sealed class GlProcessExecutionOrDeserializationException : RuntimeException {
    constructor(cause: Throwable) : super(cause)
    constructor(message: String) : super(message)
}

class GlDeserializationException(cause: JacksonException) : GlProcessExecutionOrDeserializationException(cause)

sealed class GlProcessExecutionException : GlProcessExecutionOrDeserializationException {
    constructor(message: String) : super(message)
    constructor(cause: Throwable) : super(cause)

    abstract val commandLineString: String

    class Start(
        override val commandLineString: String,
        cause: ExecutionException,
    ) : GlProcessExecutionException(cause)

    class Canceled(
        override val commandLineString: String,
        val output: ProcessOutput,
        message: String = errorMessage(commandLineString, output),
    ) : GlProcessExecutionException(message)

    class Timeout(
        override val commandLineString: String,
        val output: ProcessOutput,
    ) : GlProcessExecutionException(errorMessage(commandLineString, output))

    /** The process exited with non-zero exit code */
    class ProcessAborted(
        override val commandLineString: String,
        val output: ProcessOutput,
    ) : GlProcessExecutionException(errorMessage(commandLineString, output))

    companion object {
        fun errorMessage(commandLineString: String, output: ProcessOutput): String = """
            |Execution failed (exit code ${output.exitCode}).
            |$commandLineString
            |stdout : ${output.stdout}
            |stderr : ${output.stderr}
        """.trimMargin()
    }
}

fun GlProcessResult<ProcessOutput>.ignoreExitCode(): RsResult<ProcessOutput, GlProcessExecutionException.Start> = when (this) {
    is RsResult.Ok -> RsResult.Ok(ok)
    is RsResult.Err -> when (err) {
        is GlProcessExecutionException.Start -> RsResult.Err(err)
        is GlProcessExecutionException.Canceled -> RsResult.Ok(err.output)
        is GlProcessExecutionException.Timeout -> RsResult.Ok(err.output)
        is GlProcessExecutionException.ProcessAborted -> RsResult.Ok(err.output)
    }
}
