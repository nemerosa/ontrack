package net.nemerosa.ontrack.extension.queue.dispatching

import net.nemerosa.ontrack.extension.queue.QueueConfigProperties
import net.nemerosa.ontrack.extension.queue.QueuePayload
import net.nemerosa.ontrack.extension.queue.QueueProcessor
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.format
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.stereotype.Component

/**
 * Synchronous queue dispatching (no queue).
 *
 * Used for testing.
 */
@Component
class QueueDispatcherImpl(
    private val queueConfigProperties: QueueConfigProperties,
    private val amqpTemplate: AmqpTemplate,
) : QueueDispatcher {

    private val logger: Logger = LoggerFactory.getLogger(QueueDispatcherImpl::class.java)

    override fun <T : Any> dispatch(queueProcessor: QueueProcessor<T>, payload: T): String =
        if (sync(queueProcessor)) {
            if (queueConfigProperties.general.warnIfAsync) {
                logger.warn("Processing queuing in synchronous mode.")
            }
            queueProcessor.process(payload)
            "(sync)"
        } else {
            val queuePayload = QueuePayload.create(queueProcessor, payload)
            val routingKey = queueConfigProperties.getRoutingKey(
                queueProcessor,
                payload
            )
            val message = queuePayload.asJson().format()
            val topic = "ontrack.queue.${queueProcessor.id}"
            amqpTemplate.convertAndSend(
                topic,
                routingKey,
                message,
            )
            queuePayload.id
        }

    private fun <T : Any> sync(queueProcessor: QueueProcessor<T>): Boolean {
        return if (!queueConfigProperties.general.async) {
            true
        } else {
            val sp = queueConfigProperties.specific[queueProcessor.id]
            if (sp != null && !sp.async) {
                true
            } else {
                queueProcessor.sync
            }
        }
    }

}