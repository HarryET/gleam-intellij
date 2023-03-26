package run.gleam.gpm.toolchain.flavors

import com.intellij.openapi.util.SystemInfo
import com.intellij.util.io.isDirectory
import run.gleam.stdext.toPath
import java.nio.file.Path

class GleamMacToolchainFlavor : GleamToolchainFlavor() {

    override fun getHomePathCandidates(): Sequence<Path> {
        val path = "/usr/local/Cellar/rust/bin".toPath()
        return if (path.isDirectory()) {
            sequenceOf(path)
        } else {
            emptySequence()
        }
    }

    override fun isApplicable(): Boolean = SystemInfo.isMac
}
