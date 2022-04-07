package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel
import java.time.LocalDateTime

/**
 * Filter on webhook recorded exchanges.
 */
data class WebhookExchangeFilter(
    @APIDescription("Offset")
    val offset: Int = 0,
    @APIDescription("Number of exchanges to return at most")
    val size: Int = 10,
    @APIDescription("Name of the webhook (regular expression)")
    @APILabel("Webhook")
    val webhook: String? = null,
    @APIDescription("Payload timestamp before this")
    @APILabel("Sent before")
    val payloadBefore: LocalDateTime? = null,
    @APIDescription("Payload timestamp after this")
    @APILabel("Sent after")
    val payloadAfter: LocalDateTime? = null,
    @APIDescription("Type of payload which was sent (event, ...)")
    @APILabel("Payload type")
    val payloadType: String? = null,
    @APIDescription("Keyword in the payload")
    @APILabel("Payload keyword")
    val payloadKeyword: String? = null,
    @APIDescription("HTTP Status Code of the response")
    @APILabel("Response code")
    val responseCode: Int? = null,
    @APIDescription("Keyword in the response")
    @APILabel("Response keyword")
    val responseKeyword: String? = null,
)