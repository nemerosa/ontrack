package net.nemerosa.ontrack.extension.notifications.recording

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.model.annotations.APIDescription
import java.time.LocalDateTime

data class NotificationRecord(
    @APIDescription("Record timestamp")
    val timestamp: LocalDateTime,
    @APIDescription("Channel type")
    val channel: String,
    @APIDescription("Channel configuration")
    val channelConfig: JsonNode,
    @APIDescription("Event being notified")
    val event: JsonNode,
    @APIDescription("Result of the notification")
    val result: NotificationResult,
)
