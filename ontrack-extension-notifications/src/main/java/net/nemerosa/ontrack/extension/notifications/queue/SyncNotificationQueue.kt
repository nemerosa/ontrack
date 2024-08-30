package net.nemerosa.ontrack.extension.notifications.queue

import net.nemerosa.ontrack.extension.notifications.model.Notification
import net.nemerosa.ontrack.extension.notifications.processing.NotificationProcessingService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    prefix = "ontrack.config.extension.notifications.processing.queue",
    name = ["async"],
    havingValue = "false",
    matchIfMissing = false,
)
class SyncNotificationQueue(
    private val notificationProcessingService: NotificationProcessingService,
    private val notificationQueueItemConverter: NotificationQueueItemConverter,
) : NotificationQueue {

    override fun publish(item: Notification): Boolean {
        notificationProcessingService.process(
            item = notificationQueueItemConverter.convertFromQueue(
                notificationQueueItemConverter.convertForQueue(item)
            ),
            context = emptyMap()
        ) {}
        // OK
        return true
    }

}