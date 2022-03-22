package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.extension.notifications.channels.AbstractNotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.events.Event
import org.springframework.stereotype.Component

@Component
class WebhookNotificationChannel(
    private val webhookAdminService: WebhookAdminService,
    private val webhookExecutionService: WebhookExecutionService,
) : AbstractNotificationChannel<WebhookNotificationChannelConfig>(WebhookNotificationChannelConfig::class) {

    override fun publish(config: WebhookNotificationChannelConfig, event: Event): NotificationResult {
        // Gets the webhook
        val webhook = webhookAdminService.findWebhookByName(config.name)
            ?: return NotificationResult.notConfigured("Webhook [${config.name}] is not configured.")
        // If webhook is not enabled
        if (!webhook.enabled) {
            return NotificationResult.disabled("${webhook.name} webhook is disabled")
        }
        // Computes the payload for the event
        // TODO Use a proper message converter, see net.nemerosa.ontrack.boot.support.ResourceHttpMessageConverter
        val payload = event.asJson()
        // Runs the webhook
        return try {
            webhookExecutionService.send(webhook, payload)
            NotificationResult.ok()
        } catch (ex: Exception) {
            NotificationResult.error("Webhook failed: ${ex.message}")
        }
    }

    override val type: String = "webhook"

    override val enabled: Boolean = TODO("Use the global webhook settings")
}