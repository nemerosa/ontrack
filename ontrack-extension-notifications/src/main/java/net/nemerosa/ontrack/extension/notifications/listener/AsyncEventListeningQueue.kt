package net.nemerosa.ontrack.extension.notifications.listener

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.extension.notifications.metrics.NotificationsMetrics
import net.nemerosa.ontrack.extension.notifications.metrics.incrementForEvent
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.format
import net.nemerosa.ontrack.model.events.Event
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@ConditionalOnProperty(
    prefix = "ontrack.config.extension.notifications.dispatching.queue",
    name = ["async"],
    havingValue = "true",
    matchIfMissing = true,
)
@Transactional
class AsyncEventListeningQueue(
    private val rabbitTemplate: RabbitTemplate,
    private val meterRegistry: MeterRegistry,
) : EventListeningQueue {
    override fun publish(event: Event) {
        val message = AsyncEventListeningQueueEvent(event).asJson().format()
        rabbitTemplate.convertAndSend(
            AsyncEventListeningQueueConfig.TOPIC,
            AsyncEventListeningQueueConfig.DEFAULT,
            message,
        )
        meterRegistry.incrementForEvent(
            NotificationsMetrics.event_listening_queued,
            event
        )
    }
}