package run.gleam.lang.core.parser

import com.intellij.core.CoreInjectedLanguageManager
import com.intellij.lang.LanguageBraceMatching
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiFile
import com.intellij.testFramework.ParsingTestCase
import org.jetbrains.annotations.NonNls
import org.junit.runner.RunWith
import run.gleam.GleamJUnit4TestRunner
import run.gleam.GleamTestCase
import run.gleam.TestCase
import run.gleam.lang.GleamLanguage

@RunWith(GleamJUnit4TestRunner::class)
abstract class GleamParsingTestCaseBase(@NonNls dataPath: String) : ParsingTestCase(
    "run/gleam/lang/core/parser/fixtures/$dataPath",
    "gleam",
    /*lowerCaseFirProjectDesstLetter = */ true ,
    GleamParserDefinition()
), GleamTestCase {

    override fun setUp() {
        super.setUp()
//        addExplicitExtension(LanguageBraceMatching.INSTANCE, GleamLanguage, GleamBraceMatcher())
        project.registerService(InjectedLanguageManager::class.java, CoreInjectedLanguageManager::class.java)
    }

    override fun getTestDataPath(): String = "src/test/resources"

    override fun getTestName(lowercaseFirstLetter: Boolean): String {
        val camelCase = super.getTestName(lowercaseFirstLetter)
        return TestCase.camelOrWordsToSnake(camelCase)
    }

    protected fun hasError(file: PsiFile): Boolean {
        var hasErrors = false
        file.accept(object : PsiElementVisitor() {
            override fun visitElement(element: PsiElement) {
                if (element is PsiErrorElement) {
                    hasErrors = true
                    return
                }
                element.acceptChildren(this)
            }
        })
        return hasErrors
    }

    /** Just check that the file is parsed (somehow) without checking its AST */
    protected fun checkFileParsed() {
        val name = testName
        parseFile(name, loadFile("$name.$myFileExt"));
    }
}