package net.nemerosa.ontrack.extension.notifications.queue

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    prefix = "ontrack.extension.notifications.queue",
    name = ["async"],
    havingValue = "true",
    matchIfMissing = true,
)
class AsyncNotificationQueue : NotificationQueue {

    override fun publish(item: NotificationQueueItem): Boolean = TODO("Implement the async queue for the events")

}
