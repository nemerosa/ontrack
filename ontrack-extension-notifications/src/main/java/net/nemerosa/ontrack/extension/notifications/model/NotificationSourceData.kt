package net.nemerosa.ontrack.extension.notifications.model

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Data linking to the source of a notification")
data class NotificationSourceData(
    @APIDescription("ID of the notification source")
    val id: String,
    @APIDescription("Data allowing the identification of the notification source")
    val data: JsonNode,
)
