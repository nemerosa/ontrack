package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.model.structure.Signature

/**
 * Association of an [EventSubscription] and its ID in the storage.
 */
data class SavedEventSubscription(
    val id: String,
    val signature: Signature,
    val data: EventSubscription,
)
