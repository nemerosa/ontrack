package net.nemerosa.ontrack.extension.notifications.channels

import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
class DefaultNotificationChannelRegistry(
    private val applicationContext: ApplicationContext,
) : NotificationChannelRegistry {

    private val index: Map<String, NotificationChannel<*, *>> by lazy {
        applicationContext.getBeansOfType(NotificationChannel::class.java).values.associateBy { it.type }
    }

    override val channels: List<NotificationChannel<*, *>>
        get() = index.values.sortedBy { it.type }

    override fun findChannel(type: String): NotificationChannel<*, *>? = index[type]
}