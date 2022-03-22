package net.nemerosa.ontrack.extension.notifications.queue

import net.nemerosa.ontrack.extension.notifications.processing.NotificationProcessingService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    prefix = "ontrack.extension.notifications.queue",
    name = ["async"],
    havingValue = "false",
    matchIfMissing = false,
)
class SyncNotificationQueue(
    private val notificationProcessingService: NotificationProcessingService,
) : NotificationQueue {

    override fun publish(item: NotificationQueueItem): Boolean {
        notificationProcessingService.process(item)
        // OK
        return true
    }

}