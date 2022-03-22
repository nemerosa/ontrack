package net.nemerosa.ontrack.extension.notifications.queue

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.events.Event

data class NotificationQueueItem(
    val channel: String,
    val channelConfig: JsonNode,
    val event: Event,
)