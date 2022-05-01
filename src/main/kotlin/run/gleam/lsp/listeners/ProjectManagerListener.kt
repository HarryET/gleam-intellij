package run.gleam.lsp.listeners

import com.intellij.notification.NotificationBuilder
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import run.gleam.lsp.services.LspService
import java.io.IOException

class ProjectManagerListener : com.intellij.openapi.project.ProjectManagerListener {
    override fun projectOpened(project: Project) {
        // Ensure this isn't part of testing
        if (ApplicationManager.getApplication().isUnitTestMode) {
            return;
        }

        val lspService: LspService = ApplicationManager.getApplication().getService(
            LspService::class.java
        )

        try {
            val lspProcess = lspService.getLSPProcess()
            val lspClient = lspService.getLSPClient(lspProcess, project) ?: throw Exception("Failed to get an LSP Client for project")
            lspClient.startListening()
            NotificationBuilder("gleam.lsp", "Launched the LSP Client", NotificationType.INFORMATION)
                .buildAndNotify(project)
        } catch (e: IOException) {
            NotificationBuilder("gleam.lsp", "Failed to launch the LSP Client", NotificationType.ERROR)
                .buildAndNotify(project)
        }
    }

    override fun projectClosing(project: Project) {
        // Ensure this isn't part of testing
        if (ApplicationManager.getApplication().isUnitTestMode) {
            return;
        }

        val lspService: LspService = ApplicationManager.getApplication().getService(
            LspService::class.java
        )

        lspService.terminateLSP()
    }
}