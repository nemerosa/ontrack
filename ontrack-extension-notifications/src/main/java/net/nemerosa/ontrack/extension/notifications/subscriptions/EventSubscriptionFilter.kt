package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.structure.ProjectEntityID
import java.time.LocalDateTime

/**
 * Filter for event subscriptions.
 */
data class EventSubscriptionFilter(
    @APIDescription("Offset for the pagination")
    val offset: Int = 0,
    @APIDescription("Number of items to return")
    val size: Int = 20,
    @APIDescription("Entity subscribed to.")
    val entity: ProjectEntityID? = null,
    @APIDescription("Scope of the search (if true, includes the entity and the levels above.")
    val recursive: Boolean = true,
    @APIDescription("Filter against the channel type (exact match)")
    val channel: String? = null,
    @APIDescription("Filter against the channel configuration (channel is required)")
    val channelConfig: String? = null,
    @APIDescription("Subscriptions created before or on this date")
    val createdBefore: LocalDateTime? = null,
    @APIDescription("Name of the user who created this subscription")
    val creator: String? = null,
    @APIDescription("Event type")
    val eventType: String? = null,
)
