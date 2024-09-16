package net.nemerosa.ontrack.kdsl.spec.extension.notifications

import com.fasterxml.jackson.databind.JsonNode
import java.time.LocalDateTime

data class NotificationRecord(
    val id: String,
    val source: NotificationSourceData?,
    val timestamp: LocalDateTime?,
    val channel: String,
    val channelConfig: JsonNode,
    val event: JsonNode,
    val result: NotificationRecordResult,
)
