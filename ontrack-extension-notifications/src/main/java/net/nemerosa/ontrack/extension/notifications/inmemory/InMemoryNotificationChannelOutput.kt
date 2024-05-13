package net.nemerosa.ontrack.extension.notifications.inmemory

data class InMemoryNotificationChannelOutput(
    val sent: Boolean,
    val data: String?,
)