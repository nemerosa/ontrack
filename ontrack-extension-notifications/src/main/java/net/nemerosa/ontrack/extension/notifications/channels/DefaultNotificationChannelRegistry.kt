package net.nemerosa.ontrack.extension.notifications.channels

import org.springframework.stereotype.Component

@Component
class DefaultNotificationChannelRegistry(
) : NotificationChannelRegistry {
    override fun findChannel(type: String): NotificationChannel<*>? {
        TODO("Not yet implemented")
    }
}