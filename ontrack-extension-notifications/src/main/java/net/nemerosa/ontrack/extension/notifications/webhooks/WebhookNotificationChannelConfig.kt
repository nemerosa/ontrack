package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel

/**
 * Configuration for a webhook subscription.
 *
 * @param name Name of the webhook
 */
class WebhookNotificationChannelConfig(
    @APILabel("Webhook")
    @APIDescription("Name of the webhook to use")
    val name: String,
)