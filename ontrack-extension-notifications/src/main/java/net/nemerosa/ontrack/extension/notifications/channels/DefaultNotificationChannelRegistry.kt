package net.nemerosa.ontrack.extension.notifications.channels

import org.springframework.stereotype.Component

@Component
class DefaultNotificationChannelRegistry(
    channels: List<NotificationChannel<*,*>>,
) : NotificationChannelRegistry {

    private val index = channels.associateBy { it.type }

    override val channels: List<NotificationChannel<*, *>>
        get() = index.values.sortedBy { it.type }

    override fun findChannel(type: String): NotificationChannel<*, *>? = index[type]
}