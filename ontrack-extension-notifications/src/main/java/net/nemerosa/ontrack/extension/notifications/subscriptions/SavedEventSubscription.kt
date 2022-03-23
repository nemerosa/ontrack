package net.nemerosa.ontrack.extension.notifications.subscriptions

/**
 * Association of an [EventSubscription] and its ID in the storage.
 */
data class SavedEventSubscription(
    val id: String,
    val data: EventSubscription,
)
