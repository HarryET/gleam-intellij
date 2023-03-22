package run.gleam.gpm

object GleamPMConstants {

    const val MANIFEST_FILE = "gleam.toml"
    const val LOCK_FILE = "manifest.toml"

    object ProjectLayout {
        val sources = listOf("src", "examples")
        val tests = listOf("test", "benches")
        const val target = "target"
    }
}