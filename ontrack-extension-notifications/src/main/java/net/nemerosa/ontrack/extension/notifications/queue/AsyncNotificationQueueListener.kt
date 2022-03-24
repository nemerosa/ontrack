package net.nemerosa.ontrack.extension.notifications.queue

import net.nemerosa.ontrack.extension.notifications.processing.NotificationProcessingService
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.NameDescription
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
class AsyncNotificationQueueListener(
    private val securityService: SecurityService,
    private val notificationProcessingService: NotificationProcessingService,
    private val applicationLogService: ApplicationLogService,
    private val notificationQueueItemConverter: NotificationQueueItemConverter,
) : RabbitListenerConfigurer {

    override fun configureRabbitListeners(registrar: RabbitListenerEndpointRegistrar) {
        // TODO Channel-specific queues
        // Listener for the default
        registrar.registerEndpoint(
            createDefaultListener(),
        )
    }

    private fun createDefaultListener(): RabbitListenerEndpoint {
        val queue = "${AsyncNotificationQueueConfig.QUEUE_PREFIX}.${AsyncNotificationQueueConfig.DEFAULT}"
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
            val payload = body.parseAsJson().parse<NotificationQueueItem>()
            securityService.asAdmin {
                val notification = notificationQueueItemConverter.convertFromQueue(payload)
//           val queue = message.messageProperties.consumerQueue
//          TODO  meterRegistry.increment(
//                payload,
//                IngestionMetrics.Queue.consumedCount,
//                INGESTION_METRIC_QUEUE_TAG to queue
//            )
                // TODO ingestionHookPayloadStorage.queue(payload, queue)
                notificationProcessingService.process(notification)
            }
        } catch (any: Throwable) {
            applicationLogService.log(
                ApplicationLogEntry.error(
                    any,
                    NameDescription.nd("notifications-error", "Catch-all error in notifications processing"),
                    "Uncaught error during the notifications processing"
                )
            )
        }
    }

}