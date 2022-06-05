package net.nemerosa.ontrack.extension.av.queue

import net.nemerosa.ontrack.extension.av.AutoVersioningConfigProperties
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.extension.av.processing.AutoVersioningProcessingService
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
class AsyncAutoVersioningQueueListener(
    private val autoVersioningConfigProperties: AutoVersioningConfigProperties,
    private val autoVersioningProcessingService: AutoVersioningProcessingService,
    private val securityService: SecurityService,
    private val applicationLogService: ApplicationLogService,
) : RabbitListenerConfigurer {

    private val listener = MessageListener(::onMessage)

    override fun configureRabbitListeners(registrar: RabbitListenerEndpointRegistrar) {
        // Listener for the default queues
        (1..autoVersioningConfigProperties.queue.scale).forEach { no ->
            registrar.registerEndpoint(
                createDefaultListener(no)
            )
        }
    }

    private fun createDefaultListener(no: Int): RabbitListenerEndpoint {
        val queue = "${AsyncAutoVersioningQueueConfig.QUEUE_PREFIX}.${AsyncAutoVersioningQueueConfig.DEFAULT}.$no"
        return SimpleRabbitListenerEndpoint().configure(queue)
    }

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
            val order = body.parseAsJson().parse<AutoVersioningOrder>()
            // TODO val queue = message.messageProperties.consumerQueue
            // TODO Metrics
            securityService.asAdmin {
                // TODO Audit
                val outcome = autoVersioningProcessingService.process(order)
                // TODO Metrics
            }
        } catch (any: Throwable) {
            applicationLogService.log(
                ApplicationLogEntry.error(
                    any,
                    NameDescription.nd("auto-versioning-error", "Catch-all error in auto versioning processing"),
                    "Uncaught error during the auto versioning processing"
                )
            )
        }
    }
}
