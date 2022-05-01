package run.gleam.lsp.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import org.eclipse.lsp4j.jsonrpc.Launcher
import org.eclipse.lsp4j.launch.LSPLauncher
import org.eclipse.lsp4j.services.LanguageServer
import run.gleam.lsp.LanguageClient
import java.io.*

@Service
class LspService {
    private var process: Process? = null

    fun getLSPProcess(): Process {
        if (process != null) {
            return process as Process
        }

        try {
            val processBuilder = ProcessBuilder("gleam", "lsp")
            val newProcess = processBuilder.start()
            process = newProcess
            return newProcess
        } catch (e: IOException) {
            throw e
        }
    }

    fun getLSPClient(process: Process, project: Project): Launcher<LanguageServer>? {
        return LSPLauncher.createClientLauncher(
            LanguageClient(project),
            process.inputStream,
            process.outputStream
        )
    }

    fun terminateLSP() {
        if (process == null) {
            return;
        }

        process?.destroy()
    }
}