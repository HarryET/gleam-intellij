package run.gleam.gpm.toolchain.flavors

import com.intellij.util.io.isDirectory
import run.gleam.stdext.toPathOrNull
import java.io.File
import java.nio.file.Path

class GleamSysPathToolchainFlavor : GleamToolchainFlavor() {
    override fun getHomePathCandidates(): Sequence<Path> =
        System.getenv("PATH")
            .orEmpty()
            .split(File.pathSeparator)
            .asSequence()
            .filter { it.isNotEmpty() }
            .mapNotNull { it.toPathOrNull() }
            .filter { it.isDirectory() }
}
