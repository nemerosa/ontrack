package net.nemerosa.ontrack.kdsl.spec.extension.notifications

import com.fasterxml.jackson.databind.JsonNode

data class Subscription(
    val name: String,
    val channel: String,
    val channelConfig: JsonNode,
    val events: List<String>,
    val keywords: String?,
    val disabled: Boolean,
    val contentTemplate: String?,
)
