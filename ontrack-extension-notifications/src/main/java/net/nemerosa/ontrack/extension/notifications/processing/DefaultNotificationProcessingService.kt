package net.nemerosa.ontrack.extension.notifications.processing

import net.nemerosa.ontrack.extension.notifications.channels.NotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NotificationChannelRegistry
import net.nemerosa.ontrack.extension.notifications.queue.NotificationQueueItem
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultNotificationProcessingService(
    private val notificationChannelRegistry: NotificationChannelRegistry,
) : NotificationProcessingService {

    // TODO Notifications processing metrics

    override fun process(item: NotificationQueueItem) {
        val channel = notificationChannelRegistry.findChannel(item.channel)
        if (channel != null) {
            process(channel, item)
        }
    }

    private fun <C> process(channel: NotificationChannel<C>, item: NotificationQueueItem) {
        val validatedConfig = channel.validate(item.channelConfig)
        if (validatedConfig.config != null) {
            val result = channel.publish(validatedConfig.config, item.event)
        }
        // TODO Logs in case of error
    }

}