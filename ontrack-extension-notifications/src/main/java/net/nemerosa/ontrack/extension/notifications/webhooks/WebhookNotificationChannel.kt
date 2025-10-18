package net.nemerosa.ontrack.extension.notifications.webhooks

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.notifications.channels.AbstractNotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NoTemplate
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscriptionConfigException
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.patchString
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import org.springframework.stereotype.Component

@Component
@APIDescription("Calling an external webhook")
@Documentation(WebhookNotificationChannelConfig::class)
@Documentation(WebhookNotificationChannelOutput::class, section = "output")
@NoTemplate
class WebhookNotificationChannel(
    private val webhookAdminService: WebhookAdminService,
    private val webhookExecutionService: WebhookExecutionService,
    private val cachedSettingsService: CachedSettingsService,
) : AbstractNotificationChannel<WebhookNotificationChannelConfig, WebhookNotificationChannelOutput>(
    WebhookNotificationChannelConfig::class
) {

    override fun validateParsedConfig(config: WebhookNotificationChannelConfig) {
        if (webhookAdminService.findWebhookByName(config.name) == null) {
            throw EventSubscriptionConfigException("Webhook with name ${config.name} not found")
        }
    }

    override fun mergeConfig(
        a: WebhookNotificationChannelConfig,
        changes: JsonNode
    ) = WebhookNotificationChannelConfig(
        name = patchString(changes, a::name),
    )

    override fun publish(
        recordId: String,
        config: WebhookNotificationChannelConfig,
        event: Event,
        context: Map<String, Any>,
        template: String?,
        outputProgressCallback: (current: WebhookNotificationChannelOutput) -> WebhookNotificationChannelOutput
    ): NotificationResult<WebhookNotificationChannelOutput> {
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
            NotificationResult.ok(
                output = WebhookNotificationChannelOutput(payload = payload)
            )
        } catch (ex: Exception) {
            NotificationResult.error(
                "Webhook failed: ${ex.message}",
                output = WebhookNotificationChannelOutput(payload = payload)
            )
        }
    }

    override fun toSearchCriteria(text: String): JsonNode =
        mapOf(WebhookNotificationChannelConfig::name.name to text).asJson()

    override val type: String = "webhook"

    override val displayName: String = "Webhook"

    override val enabled: Boolean get() = cachedSettingsService.getCachedSettings(WebhookSettings::class.java).enabled
}