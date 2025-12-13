package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.graphql.support.IgnoreRef
import net.nemerosa.ontrack.graphql.support.TypeRef
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.structure.ProjectEntityID

/**
 * Filter for event subscriptions.
 */
data class EventSubscriptionFilter(
    @APIDescription("Offset for the pagination")
    @IgnoreRef
    val offset: Int = 0,
    @APIDescription("Number of items to return")
    @IgnoreRef
    val size: Int = 20,
    @APIDescription("Name of the subscription")
    val name: String? = null,
    @APIDescription("Entity subscribed to.")
    @TypeRef(suffix = "Input")
    val entity: ProjectEntityID? = null,
    @APIDescription("Scope of the search (if true, includes the entity and the levels above.")
    val recursive: Boolean? = false,
    @APIDescription("Filter against the channel type (exact match)")
    val channel: String? = null,
    @APIDescription("Filter against the channel configuration (channel is required)")
    val channelConfig: String? = null,
    @APIDescription("Event type")
    val eventType: String? = null,
    @APIDescription("Origin")
    val origin: String? = null,
) {
    fun withPage(offset: Int, size: Int) = EventSubscriptionFilter(
        offset = offset,
        size = size,
        name = name,
        entity = entity,
        recursive = recursive,
        channel = channel,
        channelConfig = channelConfig,
        eventType = eventType,
        origin = origin,
    )
}
