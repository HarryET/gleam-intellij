package run.gleam.gpm.toolchain.flavors

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.util.SystemInfo
import com.intellij.util.io.isDirectory
import run.gleam.stdext.isExecutable
import java.nio.file.Path

abstract class GleamToolchainFlavor {

    fun suggestHomePaths(): Sequence<Path> = getHomePathCandidates().filter { isValidToolchainPath(it) }

    protected abstract fun getHomePathCandidates(): Sequence<Path>

    /**
     * Flavor is added to result in [getApplicableFlavors] if this method returns true.
     * @return whether this flavor is applicable.
     */
    protected open fun isApplicable(): Boolean = true

    /**
     * Checks if the path is the name of a Gleam toolchain of this flavor.
     *
     * @param path path to check.
     * @return true if paths points to a valid home.
     */
    protected open fun isValidToolchainPath(path: Path): Boolean {
        return path.isDirectory() &&
            hasExecutable(path, "gleam")
    }

    protected open fun hasExecutable(path: Path, toolName: String): Boolean = path.hasExecutable(toolName)

    protected open fun pathToExecutable(path: Path, toolName: String): Path = path.pathToExecutable(toolName)

    companion object {
        private val EP_NAME: ExtensionPointName<GleamToolchainFlavor> =
            ExtensionPointName.create("run.gleam.toolchainFlavor")

        fun getApplicableFlavors(): List<GleamToolchainFlavor> =
            EP_NAME.extensionList.filter { it.isApplicable() }

        fun getFlavor(path: Path): GleamToolchainFlavor? =
            getApplicableFlavors().find { flavor -> flavor.isValidToolchainPath(path) }
    }
}

fun Path.hasExecutable(toolName: String): Boolean = pathToExecutable(toolName).isExecutable()

fun Path.pathToExecutable(toolName: String): Path {
    val exeName = if (SystemInfo.isWindows) "$toolName.exe" else toolName
    return resolve(exeName).toAbsolutePath()
}