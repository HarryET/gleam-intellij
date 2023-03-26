package run.gleam.gpm.toolchain.flavors

import com.intellij.openapi.util.SystemInfo
import com.intellij.util.io.isDirectory
import run.gleam.stdext.toPath
import java.nio.file.Path

class GleamUnixToolchainFlavor : GleamToolchainFlavor() {

    override fun getHomePathCandidates(): Sequence<Path> =
        sequenceOf("/usr/local/bin", "/usr/bin")
            .map { it.toPath() }
            .filter { it.isDirectory() }

    override fun isApplicable(): Boolean = SystemInfo.isUnix
}
