package net.nemerosa.ontrack.extension.notifications.queue

import net.nemerosa.ontrack.extension.notifications.NotificationsConfigProperties
import net.nemerosa.ontrack.extension.notifications.model.Notification
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.format
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    prefix = "ontrack.config.extension.notifications.processing.queue",
    name = ["async"],
    havingValue = "true",
    matchIfMissing = true,
)
class AsyncNotificationQueue(
    private val amqpTemplate: AmqpTemplate,
    private val notificationsConfigProperties: NotificationsConfigProperties,
    private val notificationQueueItemConverter: NotificationQueueItemConverter,
) : NotificationQueue {

    override fun publish(item: Notification): Boolean {
        val routingKey = AsyncNotificationQueueConfig.getRoutingKey(notificationsConfigProperties, item)
        // TODO Notification storage
        // TODO Metrics
        val message = notificationQueueItemConverter.convertForQueue(item).asJson().format()
        amqpTemplate.convertAndSend(
            AsyncNotificationQueueConfig.TOPIC,
            routingKey,
            message,
        )
        return true
    }

}
