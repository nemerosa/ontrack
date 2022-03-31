package net.nemerosa.ontrack.extension.notifications.subscriptions

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.structure.ProjectEntity

/**
 * Subscrition to an event.
 *
 * @property projectEntity Project entity to subscribe to (`null` for global events)
 * @property events List of events types to subscribe to
 * @property keywords Optional space-separated list of tokens to look for in the events
 * @property channel Type of channel to send the event to
 * @property channelConfig Specific configuration of the channel
 * @property disabled If the subscription is disabled
 */
data class EventSubscription(
    val projectEntity: ProjectEntity?,
    @APIDescription("List of events types to subscribe to")
    val events: Set<String>,
    @APIDescription("Optional space-separated list of tokens to look for in the events")
    val keywords: String?,
    @APIDescription("Type of channel to send the event to")
    val channel: String,
    val channelConfig: JsonNode,
    @APIDescription("If the subscription is disabled")
    val disabled: Boolean,
) {
    fun disabled(disabled: Boolean) = EventSubscription(projectEntity, events, keywords, channel, channelConfig, disabled)
}