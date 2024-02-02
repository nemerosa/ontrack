package net.nemerosa.ontrack.extension.notifications.model

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.events.Event

/**
 * Notification item, for one given channel and one event.
 */
data class Notification(
    val channel: String,
    val channelConfig: JsonNode,
    val event: Event,
    val template: String?,
)