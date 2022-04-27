package run.gleam.ide.colors

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.util.NlsContexts.AttributeDescriptor
import run.gleam.GleamBundle
import java.util.function.Supplier
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors as Default

@Suppress("UnstableApiUsage")
enum class GleamColor(humanName: Supplier<@AttributeDescriptor String>, default: TextAttributesKey? = null) {
    COMMENT(GleamBundle.messagePointer("settings.gleam.color.comment.normal"), Default.LINE_COMMENT),
    DOC_COMMENT(GleamBundle.messagePointer("settings.gleam.color.comment.doc"), Default.DOC_COMMENT),
    MODULE_COMMENT(GleamBundle.messagePointer("settings.gleam.color.comment.mod_doc"), Default.LINE_COMMENT),

    KEYWORD(GleamBundle.messagePointer("settings.gleam.color.keyword"), Default.KEYWORD),

    STRING(GleamBundle.messagePointer("settings.gleam.color.string"), Default.STRING),

    TYPE_IDENTIFIER(GleamBundle.messagePointer("settings.gleam.color.identifier.type"), Default.CLASS_NAME),
    ;

    val textAttributesKey = TextAttributesKey.createTextAttributesKey("run.gleam.$name", default)
    val attributesDescriptor = AttributesDescriptor(humanName, textAttributesKey)
    val testSeverity: HighlightSeverity = HighlightSeverity(name, HighlightSeverity.INFORMATION.myVal)
}