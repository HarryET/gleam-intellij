package run.gleam.ide.highlight

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import org.antlr.intellij.adaptor.xpath.XPath
import run.gleam.ide.colors.GleamColor
import run.gleam.lang.GleamLanguage


class GleamAnnotator : ExternalAnnotator<PsiFile, List<GleamAnnotator.IAnnotation>>() {
    interface IAnnotation {
        fun annotate(holder: AnnotationHolder): Unit;
    }

    companion object {
        class ColorAnnotation(private val range: TextRange, private val color: GleamColor) : IAnnotation {
            override fun annotate(holder: AnnotationHolder) {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(range)
                    .textAttributes(color.textAttributesKey)
                    .create();
            }

        }
    }


    override fun collectInformation(file: PsiFile): PsiFile {
        return file
    }

    override fun collectInformation(file: PsiFile, editor: Editor, hasErrors: Boolean): PsiFile {
        return file
    }

    override fun doAnnotate(file: PsiFile): List<IAnnotation> {
        val annotations: MutableList<IAnnotation> = ArrayList()

        // Local Variables
        XPath.findAll(GleamLanguage, file, "//function//assignment/pattern/identifier")
            .forEach { annotations.add(ColorAnnotation(it.textRange, GleamColor.LOCAL_VARIABLE)) }

        // Functions
        val funcNodes = (XPath.findAll(GleamLanguage, file, "//function") + XPath.findAll(GleamLanguage, file, "//external_function")).forEach { func ->
            XPath.findAll(GleamLanguage, func, "/function/identifier").forEach {
                annotations.add(
                    ColorAnnotation(
                        it.textRange,
                        GleamColor.FUNCTION_DECLARATION
                    )
                )
            }

            XPath.findAll(GleamLanguage, func, "//function_parameter//identifier").forEach {
                annotations.add(
                    ColorAnnotation(
                        it.textRange,
                        GleamColor.FUNCTION_PARAM
                    )
                )
            }
        }

        // Type Identifiers
        XPath.findAll(GleamLanguage, file, "//type_identifier").forEach {
            annotations.add(
                ColorAnnotation(
                    it.textRange,
                    GleamColor.TYPE_IDENTIFIER
                )
            )
        }

        // Constructors
        XPath.findAll(GleamLanguage, file, "//record").forEach { record ->
            XPath.findAll(GleamLanguage, record, "//constructor_name").forEach {
                annotations.add(
                    ColorAnnotation(
                        it.textRange,
                        GleamColor.TYPE_IDENTIFIER
                    )
                )
            }

            XPath.findAll(GleamLanguage, record, "//argument/label").forEach {
                annotations.add(
                    ColorAnnotation(
                        it.textRange,
                        GleamColor.FUNCTION_PARAM
                    )
                )
            }
        }

        // TODO error when function call on function not defined in file

        return annotations
    }

    override fun apply(file: PsiFile, annotationResult: List<IAnnotation>, holder: AnnotationHolder) {
        for (annotation in annotationResult) {
            annotation.annotate(holder)
        }
    }
}