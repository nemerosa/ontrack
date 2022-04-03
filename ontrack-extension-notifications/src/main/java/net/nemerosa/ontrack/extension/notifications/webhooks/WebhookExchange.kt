package net.nemerosa.ontrack.extension.notifications.webhooks

import java.time.LocalDateTime
import java.util.*

/**
 * Exchange for a webhook.
 */
data class WebhookExchange(
    val uuid: UUID,
    val webhook: String,
    val request: WebhookRequest,
    val response: WebhookResponse?,
    val stack: String?,
)

/**
 * Request
 */
data class WebhookRequest(
    val timestamp: LocalDateTime,
    val type: String,
    val payload: String,
)

/**
 * Response
 */
data class WebhookResponse(
    val timestamp: LocalDateTime,
    val code: Int,
    val payload: String,
)
