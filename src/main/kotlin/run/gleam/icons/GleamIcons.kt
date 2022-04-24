package run.gleam.icons

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object GleamIcons {
    val GLEAM = load("/icons/gleam.svg")

    private fun load(path: String): Icon = IconLoader.getIcon(path, GleamIcons::class.java)
}