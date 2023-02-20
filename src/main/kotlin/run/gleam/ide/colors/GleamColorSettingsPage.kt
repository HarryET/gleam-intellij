package run.gleam.ide.colors

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import com.intellij.openapi.util.io.StreamUtil
import run.gleam.GleamBundle
//import run.gleam.ide.highlight.GleamHighlighter
import run.gleam.ide.icons.GleamIcons

//class GleamColorSettingsPage : ColorSettingsPage {
//    override fun getDisplayName() = GleamBundle.message("settings.gleam.color.scheme.title")
//    override fun getIcon() = GleamIcons.GLEAM
//    override fun getAttributeDescriptors() = ATTRS
//    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY
//    override fun getHighlighter() = GleamHighlighter()
//    override fun getAdditionalHighlightingTagToDescriptorMap() = ANNOTATOR_TAGS
//    override fun getDemoText() = DEMO_TEXT
//
//    companion object {
//        private val ATTRS: Array<AttributesDescriptor> = GleamColor.values().map { it.attributesDescriptor }.toTypedArray()
//
//        private val ANNOTATOR_TAGS: Map<String, TextAttributesKey> = GleamColor.values().associateBy({ it.name }, { it.textAttributesKey })
//
//        private val DEMO_TEXT: String by lazy {
//            val stream = GleamColorSettingsPage::class.java.classLoader
//                .getResourceAsStream("run/gleam/ide/colors/highlighterDemoText.gleam")
//            StreamUtil.convertSeparators(StreamUtil.readText(stream, "UTF-8"))
//        }
//    }
//}