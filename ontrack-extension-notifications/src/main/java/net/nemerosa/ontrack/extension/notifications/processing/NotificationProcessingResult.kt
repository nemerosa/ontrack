package net.nemerosa.ontrack.extension.notifications.processing

import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult

/**
 * Result of the processing of a notification.
 *
 * @property recordId ID of the notification record
 * @property result Outcome of the notification channel
 */
data class NotificationProcessingResult<R>(
    val recordId: String,
    val result: NotificationResult<R>?,
)
