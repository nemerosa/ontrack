package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.model.structure.ProjectEntity

/**
 * Subscrition to an event.
 *
 * @property channels Channels to send this event to
 * @property projectEntity Project entity to subscribe to (`null` for global events)
 * @property events List of events types to subscribe to
 * @property eventFilter Optional space-separated list of tokens to look for in the events
 */
data class EventSubscription(
    val channels: Set<EventSubscriptionChannel>,
    val projectEntity: ProjectEntity?,
    val events: Set<String>,
    val eventFilter: String?,
)