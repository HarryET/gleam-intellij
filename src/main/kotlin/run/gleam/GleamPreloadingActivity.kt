package run.gleam

import com.intellij.openapi.application.PreloadingActivity
import com.intellij.openapi.progress.ProgressIndicator
import org.wso2.lsp4intellij.IntellijLanguageClient
import org.wso2.lsp4intellij.client.languageserver.serverdefinition.RawCommandServerDefinition

object GleamPreloadingActivity : PreloadingActivity() {
    override fun preload(indicator: ProgressIndicator) {
        IntellijLanguageClient.addServerDefinition(RawCommandServerDefinition("gleam", arrayOf("gleam", "lsp")))
    }
}