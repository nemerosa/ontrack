package net.nemerosa.ontrack.extension.notifications.channels

fun NotificationChannelRegistry.getChannel(type: String) =
    findChannel(type) ?: throw NotificationChannelNotFoundException(type)
