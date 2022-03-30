package net.nemerosa.ontrack.extension.notifications.subscriptions

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.structure.ProjectEntity

/**
 * Subscrition to an event.
 *
 * @property projectEntity Project entity to subscribe to (`null` for global events)
 * @property events List of events types to subscribe to
 * @property keywords Optional space-separated list of tokens to look for in the events
 * @property channel Type of channel to send the event to
 * @property channelConfig Specific configuration of the channel
 */
data class EventSubscription(
    val projectEntity: ProjectEntity?,
    val events: Set<String>,
    val keywords: String?,
    val channel: String,
    val channelConfig: JsonNode,
)