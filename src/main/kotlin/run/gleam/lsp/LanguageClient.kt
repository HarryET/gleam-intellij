package run.gleam.lsp

import com.intellij.notification.NotificationBuilder
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import org.eclipse.lsp4j.*
import org.jetbrains.rpc.LOG
import java.util.concurrent.CompletableFuture

class LanguageClient(private val project: Project): org.eclipse.lsp4j.services.LanguageClient {
    override fun telemetryEvent(`object`: Any?) {
        LOG.debug(`object`.toString())
    }

    override fun publishDiagnostics(diagnostics: PublishDiagnosticsParams?) {
        TODO("Not yet implemented")
    }

    override fun showMessage(messageParams: MessageParams?) {
        when (messageParams?.type) {
            MessageType.Info -> NotificationBuilder("gleam.lsp", messageParams.message, NotificationType.INFORMATION).buildAndNotify(project)
            MessageType.Log -> NotificationBuilder("gleam.lsp", messageParams.message, NotificationType.INFORMATION).buildAndNotify(project)
            MessageType.Error -> NotificationBuilder("gleam.lsp", messageParams.message, NotificationType.ERROR).buildAndNotify(project)
            MessageType.Warning -> NotificationBuilder("gleam.lsp", messageParams.message, NotificationType.WARNING).buildAndNotify(project)
            else -> {}
        }
    }

    override fun showMessageRequest(requestParams: ShowMessageRequestParams?): CompletableFuture<MessageActionItem> {
        TODO("Not yet implemented")
    }

    override fun logMessage(message: MessageParams?) {
        when (message?.type) {
            MessageType.Info -> LOG.info(message.message)
            MessageType.Log -> LOG.info(message.message)
            MessageType.Error -> LOG.error(message.message)
            MessageType.Warning -> LOG.warn(message.message)
            else -> {}
        }
    }

    override fun registerCapability(params: RegistrationParams?): CompletableFuture<Void> {
        LOG.info("Registering Capabilities!")
        return super.registerCapability(params)
    }
}