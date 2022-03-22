package net.nemerosa.ontrack.extension.notifications.channels

data class NotificationResult(
    val type: NotificationResultType,
    val message: String?,
)