package net.nemerosa.ontrack.extension.workflows.notifications

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.annotations.APIDescription

data class WorkflowNotificationChannelNodeData(
    @APIDescription("Notification channel ID")
    val channel: String,
    @APIDescription("Notification channel configuration")
    val channelConfig: JsonNode,
    @APIDescription("Optional template for the notification")
    val template: String?,
)
