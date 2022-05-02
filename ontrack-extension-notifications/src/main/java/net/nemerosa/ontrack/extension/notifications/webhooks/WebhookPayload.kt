package net.nemerosa.ontrack.extension.notifications.webhooks

import java.util.*

/**
 * Minimal requirements for the payload of a webhook.
 */
data class WebhookPayload<T>(
    val uuid: UUID = UUID.randomUUID(),
    val type: String,
    val data: T,
)
