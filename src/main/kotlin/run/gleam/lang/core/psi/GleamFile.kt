package run.gleam.lang.core.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import run.gleam.lang.GleamFileType
import run.gleam.lang.GleamLanguage

class GleamFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, GleamLanguage) {
    override fun getFileType(): FileType = GleamFileType
}