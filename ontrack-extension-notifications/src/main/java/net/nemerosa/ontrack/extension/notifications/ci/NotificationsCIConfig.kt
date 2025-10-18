package net.nemerosa.ontrack.extension.notifications.ci

data class NotificationsCIConfig(
    val notifications: List<NotificationsCIConfigItem> = emptyList(),
)