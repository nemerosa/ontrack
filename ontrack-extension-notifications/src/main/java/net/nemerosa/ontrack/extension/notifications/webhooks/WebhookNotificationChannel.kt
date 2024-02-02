package net.nemerosa.ontrack.extension.notifications.webhooks

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.notifications.channels.AbstractNotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.selectionOfString
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import org.springframework.stereotype.Component

@Component
class WebhookNotificationChannel(
    private val webhookAdminService: WebhookAdminService,
    private val webhookExecutionService: WebhookExecutionService,
    private val cachedSettingsService: CachedSettingsService,
    private val securityService: SecurityService,
) : AbstractNotificationChannel<WebhookNotificationChannelConfig>(WebhookNotificationChannelConfig::class) {

    override fun publish(
        config: WebhookNotificationChannelConfig,
        event: Event,
        template: String?,
    ): NotificationResult {
        // Gets the webhook
        val webhook = webhookAdminService.findWebhookByName(config.name)
            ?: return NotificationResult.notConfigured("Webhook [${config.name}] is not configured.")
        // If webhook is not enabled
        if (!webhook.enabled) {
            return NotificationResult.disabled("${webhook.name} webhook is disabled")
        }
        // Computes the payload for the event
        val payload = WebhookPayload(type = "event", data = event)
        // Runs the webhook
        return try {
            webhookExecutionService.send(webhook, payload)
            NotificationResult.ok(payload.uuid.toString())
        } catch (ex: Exception) {
            NotificationResult.error("Webhook failed: ${ex.message}", id = payload.uuid.toString())
        }
    }

    override fun toSearchCriteria(text: String): JsonNode =
        mapOf(WebhookNotificationChannelConfig::name.name to text).asJson()

    override fun toText(config: WebhookNotificationChannelConfig): String = config.name

    override fun getForm(c: WebhookNotificationChannelConfig?): Form = Form.create()
        .selectionOfString(
            WebhookNotificationChannelConfig::name,
            securityService.asAdmin {
                webhookAdminService.webhooks.map { it.name }.sorted()
            },
            c?.name
        )

    override val type: String = "webhook"

    override val enabled: Boolean get() = cachedSettingsService.getCachedSettings(WebhookSettings::class.java).enabled
}