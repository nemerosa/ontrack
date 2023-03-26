package net.nemerosa.ontrack.extension.queue.config

import net.nemerosa.ontrack.extension.queue.QueueConfigProperties
import net.nemerosa.ontrack.extension.queue.QueuePayload
import net.nemerosa.ontrack.extension.queue.QueueProcessor
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
class QueueListener(
    private val queueConfigProperties: QueueConfigProperties,
    private val queueProcessors: List<QueueProcessor<*>>,
    private val securityService: SecurityService,
    private val applicationLogService: ApplicationLogService,
) : RabbitListenerConfigurer {

    override fun configureRabbitListeners(registrar: RabbitListenerEndpointRegistrar) {
        queueProcessors.forEach { queueProcessor ->
            configureListener(registrar, queueProcessor)
        }
    }

    private fun configureListener(
        registrar: RabbitListenerEndpointRegistrar,
        queueProcessor: QueueProcessor<*>
    ) {
        // Default listeners
        val scale = queueConfigProperties.specific[queueProcessor.id]?.scale ?: 1
        if (scale > 1) {
            (1..scale).forEach { no ->
                val index = no - 1 // Starting at 0
                registrar.registerEndpoint(
                    createDefaultListener(queueProcessor, index)
                )
            }
        } else {
            registrar.registerEndpoint(
                createDefaultListener(queueProcessor, 0)
            )
        }
    }

    private fun createDefaultListener(
        queueProcessor: QueueProcessor<*>,
        index: Int,
    ): RabbitListenerEndpoint {
        val queue = "ontrack.queue.${queueProcessor.id}.$index"
        return SimpleRabbitListenerEndpoint().configure(queue, queueProcessor)
    }

    private fun SimpleRabbitListenerEndpoint.configure(
        queue: String,
        queueProcessor: QueueProcessor<*>
    ): SimpleRabbitListenerEndpoint {
        id = queue
        setQueueNames(queue)
        concurrency = "1-1" // No concurrency, we want the events to be processed in turn for a given queue
        messageListener = createMessageListener(queueProcessor)
        return this
    }

    private fun <T : Any> createMessageListener(queueProcessor: QueueProcessor<T>) =
        QPMessageListener<T>(queueProcessor)

    private inner class QPMessageListener<T : Any>(
        private val queueProcessor: QueueProcessor<T>
    ) : MessageListener {
        override fun onMessage(message: Message) {
            try {
                val body = message.body.toString(Charsets.UTF_8).parseAsJson()
                val qp = QueuePayload.parse(body)

                // Checks the processor
                if (qp.processor != queueProcessor.id) {
                    throw IllegalStateException("Queue processor ${queueProcessor.id} received message for ${qp.processor}...")
                }

                // Parsing the payload
                val payload = qp.parse(queueProcessor.payloadType)

                val queue = message.messageProperties.consumerQueue
                // TOO Metrics
                securityService.asAdmin {
                    // TODO Audit
                    // Processing
                    queueProcessor.process(payload)
                }
            } catch (any: Throwable) {
                applicationLogService.log(
                    ApplicationLogEntry.error(
                        any,
                        NameDescription.nd(
                            "queue-error",
                            "Catch-all error in queue processing"
                        ),
                        "Uncaught error during the queue processing"
                    ).withDetail("id", queueProcessor.id)
                )
            }
        }

    }

}

