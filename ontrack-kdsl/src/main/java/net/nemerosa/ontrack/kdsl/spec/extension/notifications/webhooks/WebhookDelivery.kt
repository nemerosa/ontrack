package net.nemerosa.ontrack.kdsl.spec.extension.notifications.webhooks

import java.time.LocalDateTime
import java.util.*

data class WebhookDelivery(
    /** Unique ID for the exchange */
    val uuid: UUID,
    /** Webhook name */
    val webhook: String,
    /** Request sent to the webhook */
    val request: WebhookRequest,
    /** Response received from the webhook */
    val response: WebhookResponse?,
    /** Error raised during the exchange */
    val stack: String?,
)

/**
 * Request sent to the webhook
 */
data class WebhookRequest(
    /** Request timestamp */
    val timestamp: LocalDateTime,
    /** Type of payload */
    val type: String,
    /** Payload sent to the webhook */
    val payload: String,
)

/**
 * Response received from the webhook
 */
data class WebhookResponse(
    /** Response timestamp */
    val timestamp: LocalDateTime,
    /** Response HTTP status code */
    val code: Int,
    /** Response payload */
    val payload: String,
)
