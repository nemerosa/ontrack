package net.nemerosa.ontrack.extension.notifications.queue

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.model.support.NameValue

data class NotificationQueueItem(
    val channel: String,
    val channelConfig: JsonNode,
    val eventType: String,
    val signature: Signature?,
    val entities: Map<ProjectEntityType, Int>,
    val ref: ProjectEntityType?,
    val values: Map<String, NameValue>,
)
