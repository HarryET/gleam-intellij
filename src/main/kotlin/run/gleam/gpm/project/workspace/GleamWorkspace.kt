package run.gleam.gpm.project.workspace

import com.intellij.openapi.util.UserDataHolderEx
import com.intellij.openapi.vfs.VirtualFile
import java.nio.file.Path

interface GleamWorkspace {
    val manifestPath: Path
    val contentRoot: Path get() = manifestPath.parent

    val workspaceRoot: VirtualFile?

    /**
     * Flatten list of packages including workspace members, dependencies. Use `packages.filter { it.origin == PackageOrigin.WORKSPACE }` to
     * obtain workspace members.
     */
    val packages: Collection<Package>

    interface Package : UserDataHolderEx {
        val contentRoot: VirtualFile?
        val rootDirectory: Path

        val id: String
        val name: String
        val normName: String get() = name.replace('-', '_')

        val version: String

        val source: String?
        val origin: PackageOrigin

        val target: Target

        val dependencies: Collection<Dependency>

        val workspace: GleamWorkspace

        val env: Map<String, String>

        val outDir: VirtualFile?

        fun findDependency(normName: String): Target? =
            if (this.normName == normName) target else dependencies.find { it.name == normName }?.pkg?.target
    }

    interface Target {
        val name: String

        // target name must be a valid Gleam identifier, so normalize it by mapping `-` to `_`
        // https://github.com/rust-lang/cargo/blob/ece4e963a3054cdd078a46449ef0270b88f74d45/src/cargo/core/manifest.rs#L299
        val normName: String get() = name.replace('-', '_')

        val kind: TargetKind

        // val crateRoot: VirtualFile?   probably don't need this...

        val pkg: Package

        val doctest: Boolean
    }
    interface Dependency {
        val pkg: Package
        val name: String
        val depKinds: List<DepKindInfo>
    }

    data class DepKindInfo(
        val kind: DepKind,
        val target: String? = null
    )

    enum class DepKind(val cargoName: String?) {
        // [dependencies]
        Normal(null),

        // [dev-dependencies]
        Development("dev"),
    }

    sealed class TargetKind(val name: String) {
        object Erlang : TargetKind("erlang")
        object Javascript : TargetKind("javascript")

        val isErlang: Boolean get() = this == Erlang
        val isJavascript: Boolean get() = this == Javascript
    }
}