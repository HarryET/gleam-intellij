package run.gleam.lang.core.stubs

import com.intellij.psi.stubs.PsiFileStubImpl
import com.intellij.psi.tree.IStubFileElementType
import run.gleam.lang.GleamLanguage
import run.gleam.lang.core.parser.GleamParserDefinition
import run.gleam.lang.core.psi.GleamFile

class GleamFileStub(
    file: GleamFile?,
    private val flags: Int
) : PsiFileStubImpl<GleamFile>(file) {
    object Type : IStubFileElementType<GleamFileStub>(GleamLanguage) {
        // Bump this number if Stub structure changes
        private const val STUB_VERSION = 1
        override fun getStubVersion(): Int = GleamParserDefinition.PARSER_VERSION + STUB_VERSION

        override fun getExternalId(): String = "Gleam.file"
    }
}

//fun factory(name: String): GleamStubElementType<*, *> = when (name) {
//    else -> error("Unknown element $name")
//}