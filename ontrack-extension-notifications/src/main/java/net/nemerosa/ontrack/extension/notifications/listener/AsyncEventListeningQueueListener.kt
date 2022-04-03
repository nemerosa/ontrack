package net.nemerosa.ontrack.extension.notifications.listener

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.extension.notifications.metrics.NotificationsMetrics
import net.nemerosa.ontrack.extension.notifications.metrics.incrementForEvent
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.metrics.increment
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.support.ApplicationLogEntry
import net.nemerosa.ontrack.model.support.ApplicationLogService
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageListener
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpoint
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar
import org.springframework.stereotype.Component

@Component
class AsyncEventListeningQueueListener(
    private val eventFactory: EventFactory,
    private val structureService: StructureService,
    private val securityService: SecurityService,
    private val eventListeningService: EventListeningService,
    private val applicationLogService: ApplicationLogService,
    private val meterRegistry: MeterRegistry,
) : RabbitListenerConfigurer {

    override fun configureRabbitListeners(registrar: RabbitListenerEndpointRegistrar) {
        registrar.registerEndpoint(
            createDefaultListener(),
        )
    }

    private fun createDefaultListener(): RabbitListenerEndpoint {
        val queue = "${AsyncEventListeningQueueConfig.QUEUE_PREFIX}.${AsyncEventListeningQueueConfig.DEFAULT}"
        return SimpleRabbitListenerEndpoint().configure(queue)
    }

    private val listener = MessageListener(::onMessage)

    private fun SimpleRabbitListenerEndpoint.configure(
        queue: String,
    ): SimpleRabbitListenerEndpoint {
        id = queue
        setQueueNames(queue)
        concurrency = "1-1" // No concurrency, we want the events to be processed in turn
        messageListener = listener
        return this
    }

    private fun onMessage(message: Message) {
        try {
            val body = message.body.toString(Charsets.UTF_8)
            val payload = body.parseAsJson().parse<AsyncEventListeningQueueEvent>()
            securityService.asAdmin(Runnable {
                val event = payload.toEvent(eventFactory, structureService)
                meterRegistry.incrementForEvent(
                    NotificationsMetrics.event_listening_dequeued,
                    event
                )
                eventListeningService.onEvent(event)
            })
        } catch (any: Throwable) {
            meterRegistry.increment(
                NotificationsMetrics.event_listening_dequeued_error
            )
            applicationLogService.log(
                ApplicationLogEntry.error(
                    any,
                    NameDescription.nd("notifications-dispatching-error",
                        "Catch-all error in notifications dispatching"),
                    "Uncaught error during the notifications dispatching"
                )
            )
        }
    }

}