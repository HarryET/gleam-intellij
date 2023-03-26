package run.gleam.gpm.toolchain.flavors

import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.util.io.FileUtil
import com.intellij.util.io.isDirectory
import run.gleam.stdext.toPath
import run.gleam.stdext.list
import java.nio.file.Path
import kotlin.io.path.exists

class GleamWinToolchainFlavor : GleamToolchainFlavor() {

    override fun getHomePathCandidates(): Sequence<Path> {
        val programFiles = System.getenv("ProgramFiles")?.toPath() ?: return emptySequence()
        if (!programFiles.exists() || !programFiles.isDirectory()) return emptySequence()
        return programFiles.list()
            .filter { it.isDirectory() }
            .filter {
                val name = FileUtil.getNameWithoutExtension(it.fileName.toString())
                name.lowercase().startsWith("gleam")
            }
            .map { it.resolve("bin") }
            .filter { it.isDirectory() }
    }

    override fun isApplicable(): Boolean = SystemInfo.isWindows
}
