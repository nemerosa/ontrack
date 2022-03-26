package net.nemerosa.ontrack.extension.notifications.listener

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.format
import net.nemerosa.ontrack.model.events.Event
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    prefix = "ontrack.config.extension.notifications.dispatching.queue",
    name = ["async"],
    havingValue = "true",
    matchIfMissing = true,
)
class AsyncEventListeningQueue(
    private val amqpTemplate: AmqpTemplate,
) : EventListeningQueue {
    override fun publish(event: Event) {
        val message = AsyncEventListeningQueueEvent(event).asJson().format()
        amqpTemplate.convertAndSend(
            AsyncEventListeningQueueConfig.TOPIC,
            AsyncEventListeningQueueConfig.DEFAULT,
            message,
        )
    }
}