package net.nemerosa.ontrack.extension.notifications.recording

import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.events.Event

data class NotificationRecord(
    @APIDescription("Channel type")
    val channel: String,
    @APIDescription("Channel configuration")
    val channelConfig: Any,
    @APIDescription("Event being notified")
    val event: Event,
    @APIDescription("Result of the notification")
    val result: NotificationResult,
)
