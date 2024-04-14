package net.nemerosa.ontrack.extension.notifications.recording

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResultType
import net.nemerosa.ontrack.model.annotations.APIDescription

@JsonIgnoreProperties(ignoreUnknown = true)
data class NotificationRecordResult(
    @APIDescription("Type of result")
    val type: NotificationResultType,
    @APIDescription("Result message")
    val message: String?,
    @APIDescription("Output of the channel")
    val output: JsonNode?,
)
