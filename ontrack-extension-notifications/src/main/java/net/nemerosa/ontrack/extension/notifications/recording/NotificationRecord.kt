package net.nemerosa.ontrack.extension.notifications.recording

import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.model.events.Event

data class NotificationRecord(
    val channel: String,
    val channelConfig: Any,
    val event: Event,
    val result: NotificationResult,
)
