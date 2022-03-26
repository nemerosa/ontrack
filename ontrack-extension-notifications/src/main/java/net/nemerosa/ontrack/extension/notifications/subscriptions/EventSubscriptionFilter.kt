package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.structure.ProjectEntityID

/**
 * Filter for event subscriptions.
 */
data class EventSubscriptionFilter(
    @APIDescription("Offset for the pagination")
    val offset: Int = 0,
    @APIDescription("Number of items to return")
    val size: Int = 20,
    @APIDescription("Scope of the search")
    val scope: EventSubscriptionFilterScope = EventSubscriptionFilterScope.ALL,
    @APIDescription("Entity subscribed to. Required if scope = ENTITY.")
    val entity: ProjectEntityID? = null,
    @APIDescription("Filter against the channel type (exact match)")
    val channel: String? = null,
)
