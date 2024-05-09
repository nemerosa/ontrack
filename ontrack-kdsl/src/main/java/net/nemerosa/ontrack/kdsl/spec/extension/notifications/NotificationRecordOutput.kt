package net.nemerosa.ontrack.kdsl.spec.extension.notifications

import com.fasterxml.jackson.databind.JsonNode

data class NotificationRecordOutput(
    val type: String,
    val message: String?,
    val output: JsonNode?,
)
