package run.gleam.lang.core.parser

import com.intellij.psi.PsiFile

class GleamCompleteParsingTestCase : GleamParsingTestCaseBase("complete") {
    fun `test fn`() = doTest(true)

    override fun checkResult(targetDataName: String, file: PsiFile) {
        super.checkResult(targetDataName, file)
        check(!hasError(file)) {
            "Error in well formed file ${file.name}"
        }
    }
}