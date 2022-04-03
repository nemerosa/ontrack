package net.nemerosa.ontrack.extension.notifications.queue

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.extension.notifications.NotificationsConfigProperties
import net.nemerosa.ontrack.extension.notifications.metrics.NotificationsMetrics
import net.nemerosa.ontrack.extension.notifications.model.Notification
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.format
import net.nemerosa.ontrack.model.metrics.increment
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
    private val meterRegistry: MeterRegistry,
) : NotificationQueue {

    override fun publish(item: Notification): Boolean {
        val routingKey = AsyncNotificationQueueConfig.getRoutingKey(notificationsConfigProperties, item)
        meterRegistry.increment(
            NotificationsMetrics.event_dispatching_queued,
            "event" to item.event.eventType.id,
            "channel" to item.channel,
            "routing" to routingKey,
        )
        val message = notificationQueueItemConverter.convertForQueue(item).asJson().format()
        amqpTemplate.convertAndSend(
            AsyncNotificationQueueConfig.TOPIC,
            routingKey,
            message,
        )
        return true
    }

}
