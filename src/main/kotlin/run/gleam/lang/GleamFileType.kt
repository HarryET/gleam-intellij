package run.gleam.lang

import com.intellij.openapi.fileTypes.LanguageFileType
import run.gleam.icons.GleamIcons
import javax.swing.Icon

object GleamFileType : LanguageFileType(GleamLanguage) {
    override fun getName(): String = "Gleam"

    override fun getDescription(): String = "The Gleam programming language"

    override fun getDefaultExtension(): String = "gleam"

    override fun getIcon(): Icon = GleamIcons.GLEAM
}