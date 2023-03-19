package run.gleam.lang.core.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.util.LocalTimeCounter
import run.gleam.lang.GleamFileType

class GleamPsiFactory(
    private val project: Project,
    private val markGenerated: Boolean = true,
    private val eventSystemEnabled: Boolean = false
) {
    fun createFile(text: CharSequence): GleamFile = createPsiFile(text) as GleamFile

    /**
     * Returns [PsiPlainTextFile] if [text] is too large.
     * Otherwise returns [RsFile].
     */
    fun createPsiFile(text: CharSequence): PsiFile =
        PsiFileFactory.getInstance(project)
            .createFileFromText(
                "DUMMY.gleam",
                GleamFileType,
                text,
                /*modificationStamp =*/ LocalTimeCounter.currentTime(), // default value
                /*eventSystemEnabled =*/ eventSystemEnabled, // `false` by default
                /*markAsCopy =*/ markGenerated // `true` by default
            )

}

private fun String.iff(cond: Boolean) = if (cond) "$this " else " "
