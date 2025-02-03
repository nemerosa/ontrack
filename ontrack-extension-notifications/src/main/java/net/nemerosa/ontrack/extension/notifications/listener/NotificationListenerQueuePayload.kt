package net.nemerosa.ontrack.extension.notifications.listener

import net.nemerosa.ontrack.model.events.SerializableEvent

data class NotificationListenerQueuePayload(
    val serializedEvent: SerializableEvent,
)
