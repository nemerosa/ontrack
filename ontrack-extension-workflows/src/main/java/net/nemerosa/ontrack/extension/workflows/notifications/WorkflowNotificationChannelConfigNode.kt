package net.nemerosa.ontrack.extension.workflows.notifications

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowParentNode

data class WorkflowNotificationChannelConfigNode(
    val id: String,
    val parents: List<WorkflowParentNode>,
    val channel: String,
    val channelConfig: JsonNode,
    val template: String?,
)
