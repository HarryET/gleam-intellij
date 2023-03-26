package run.gleam.gpm.toolchain

import com.intellij.execution.configuration.EnvironmentVariablesData
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.PtyCommandLine
import com.intellij.execution.wsl.WslPath
import com.intellij.util.net.HttpConfigurable
import run.gleam.gpm.toolchain.flavors.GleamToolchainFlavor
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists

abstract class GleamToolchainBase(val location: Path) {
    val presentableLocation: String get() = pathToExecutable("gleam").toString()

    abstract val fileSeparator: String

    abstract val executionTimeoutInMilliseconds: Int

    fun looksLikeValidToolchain(): Boolean = GleamToolchainFlavor.getFlavor(location) != null

    /**
     * Patches passed command line to make it runnable in remote context.
     */
    abstract fun patchCommandLine(commandLine: GeneralCommandLine): GeneralCommandLine

    abstract fun toLocalPath(remotePath: String): String

    abstract fun toRemotePath(localPath: String): String

    abstract fun expandUserHome(remotePath: String): String

    abstract fun getExecutableName(toolName: String): String

    // for executables from toolchain
    abstract fun pathToExecutable(toolName: String): Path

    // for executables installed using `cargo install`
    fun pathToCargoExecutable(toolName: String): Path {
        // Binaries installed by `cargo install` (e.g. Grcov, Evcxr) are placed in ~/.cargo/bin by default:
        // https://doc.rust-lang.org/cargo/commands/cargo-install.html
        // But toolchain root may be different (e.g. on Arch Linux it is usually /usr/bin)
        val exePath = pathToExecutable(toolName)
        if (exePath.exists()) return exePath
        val cargoBin = expandUserHome("~/.cargo/bin")
        val exeName = getExecutableName(toolName)
        return Paths.get(cargoBin, exeName)
    }

    abstract fun hasExecutable(exec: String): Boolean

    abstract fun hasCargoExecutable(exec: String): Boolean

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GleamToolchainBase) return false

        if (location != other.location) return false

        return true
    }

    override fun hashCode(): Int = location.hashCode()

    fun createGeneralCommandLine(
        executable: Path,
        workingDirectory: Path,
        redirectInputFrom: File?,
        backtraceMode: BacktraceMode,
        environmentVariables: EnvironmentVariablesData,
        parameters: List<String>,
        emulateTerminal: Boolean,
        withSudo: Boolean,
        patchToRemote: Boolean = true,
        http: HttpConfigurable = HttpConfigurable.getInstance()
    ): GeneralCommandLine {
        var commandLine = GeneralCommandLine(executable, withSudo)
            .withWorkDirectory(workingDirectory)
            .withInput(redirectInputFrom)
            .withEnvironment("TERM", "ansi")
            .withParameters(parameters)
            .withCharset(Charsets.UTF_8)
            .withRedirectErrorStream(true)
        withProxyIfNeeded(commandLine, http)

        when (backtraceMode) {
            BacktraceMode.SHORT -> commandLine.withEnvironment(CargoConstants.RUST_BACKTRACE_ENV_VAR, "short")
            BacktraceMode.FULL -> commandLine.withEnvironment(CargoConstants.RUST_BACKTRACE_ENV_VAR, "full")
            BacktraceMode.NO -> Unit
        }

        environmentVariables.configureCommandLine(commandLine, true)

        if (emulateTerminal) {
            commandLine = PtyCommandLine(commandLine)
                .withInitialColumns(PtyCommandLine.MAX_COLUMNS)
                .withConsoleMode(false)
        }

        if (patchToRemote) {
            commandLine = patchCommandLine(commandLine)
        }

        return commandLine
    }

    companion object {
        val MIN_SUPPORTED_TOOLCHAIN = "1.56.0".parseSemVer()

        @JvmOverloads
        fun suggest(projectDir: Path? = null): GleamToolchainBase? {
            val distribution = projectDir?.let { WslPath.getDistributionByWindowsUncPath(it.toString()) }
            val toolchain = distribution
                ?.getHomePathCandidates()
                ?.filter { GleamToolchainFlavor.getFlavor(it) != null }
                ?.mapNotNull { RsToolchainProvider.getToolchain(it.toAbsolutePath()) }
                ?.firstOrNull()
            if (toolchain != null) return toolchain

            return GleamToolchainFlavor.getApplicableFlavors()
                .asSequence()
                .flatMap { it.suggestHomePaths() }
                .mapNotNull { RsToolchainProvider.getToolchain(it.toAbsolutePath()) }
                .firstOrNull()
        }
    }
}
