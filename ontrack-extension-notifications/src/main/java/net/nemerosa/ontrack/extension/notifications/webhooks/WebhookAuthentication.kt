package net.nemerosa.ontrack.extension.notifications.webhooks

import com.fasterxml.jackson.databind.JsonNode

data class WebhookAuthentication(
    val type: String,
    val config: JsonNode,
)