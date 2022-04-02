package net.nemerosa.ontrack.extension.notifications.webhooks

/**
 * Minimal requirements for the payload of a webhook.
 */
data class WebhookPayload<T>(
    val type: String,
    val data: T,
)
