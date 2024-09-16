package net.nemerosa.ontrack.kdsl.spec.extension.notifications

import com.fasterxml.jackson.databind.JsonNode

data class NotificationSourceData(
    val id: String,
    val data: JsonNode,
)
