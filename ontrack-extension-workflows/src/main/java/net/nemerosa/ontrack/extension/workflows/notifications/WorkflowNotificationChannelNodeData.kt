package net.nemerosa.ontrack.extension.workflows.notifications

import com.fasterxml.jackson.databind.JsonNode

data class WorkflowNotificationChannelNodeData(
    val channel: String,
    val channelConfig: JsonNode,
    val template: String?,
)
