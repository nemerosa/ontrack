package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.graphql.support.TypeRef
import net.nemerosa.ontrack.model.annotations.APIDescription
import java.time.LocalDateTime
import java.util.*

/**
 * Exchange for a webhook.
 */
@APIDescription("Webhook exchange")
data class WebhookExchange(
    @APIDescription("Unique ID for the exchange")
    val uuid: UUID,
    @APIDescription("Webhook name")
    val webhook: String,
    @APIDescription("Request sent to the webhook")
    @TypeRef(embedded = true)
    val request: WebhookRequest,
    @APIDescription("Response received from the webhook")
    @TypeRef(embedded = true)
    val response: WebhookResponse?,
    @APIDescription("Error raised during the exchange")
    val stack: String?,
)

/**
 * Request
 */
@APIDescription("Request sent to the webhook")
data class WebhookRequest(
    @APIDescription("Request timestamp")
    val timestamp: LocalDateTime,
    @APIDescription("Type of payload")
    val type: String,
    @APIDescription("Payload sent to the webhook")
    val payload: String,
)

/**
 * Response
 */
@APIDescription("Response received from the webhook")
data class WebhookResponse(
    @APIDescription("Response timestamp")
    val timestamp: LocalDateTime,
    @APIDescription("Response HTTP status code")
    val code: Int,
    @APIDescription("Response payload")
    val payload: String,
)
