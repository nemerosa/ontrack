package net.nemerosa.ontrack.extension.av.queue

import com.rabbitmq.client.Channel
import net.nemerosa.ontrack.extension.av.AutoVersioningConfigProperties
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditQueryService
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditService
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.extension.av.metrics.AutoVersioningMetricsService
import net.nemerosa.ontrack.extension.av.processing.AutoVersioningProcessingService
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.support.ApplicationLogEntry
import net.nemerosa.ontrack.model.support.ApplicationLogService
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.AmqpAdmin
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpoint
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.*


@Component
class AsyncAutoVersioningQueueListener(
    private val autoVersioningConfigProperties: AutoVersioningConfigProperties,
    private val autoVersioningProcessingService: AutoVersioningProcessingService,
    private val autoVersioningAuditService: AutoVersioningAuditService,
    private val autoVersioningAuditQueryService: AutoVersioningAuditQueryService,
    private val securityService: SecurityService,
    private val applicationLogService: ApplicationLogService,
    private val metrics: AutoVersioningMetricsService,
    private val amqpAdmin: AmqpAdmin,
) : RabbitListenerConfigurer, AutoVersioningQueueStats {

    private val logger: Logger = LoggerFactory.getLogger(AsyncAutoVersioningQueueListener::class.java)

    override val pendingOrders: Int
        get() =
            (1..autoVersioningConfigProperties.queue.scale).sumOf { no ->
                val queue = getDefaultQueueName(no)
                val queueProperties: Properties? = amqpAdmin.getQueueProperties(queue)
                queueProperties?.get("QUEUE_MESSAGE_COUNT")?.toString()?.toInt(10) ?: 0
            } + autoVersioningConfigProperties.queue.projects.sumOf { config ->
                val queue = getProjectQueueName(config)
                val queueProperties: Properties? = amqpAdmin.getQueueProperties(queue)
                queueProperties?.get("QUEUE_MESSAGE_COUNT")?.toString()?.toInt(10) ?: 0
            }

    override fun configureRabbitListeners(registrar: RabbitListenerEndpointRegistrar) {
        // Listener for the default queues
        (1..autoVersioningConfigProperties.queue.scale).forEach { no ->
            registrar.registerEndpoint(
                createDefaultListener(no)
            )
        }
        // Listener for the project queues
        autoVersioningConfigProperties.queue.projects.forEach { config ->
            registrar.registerEndpoint(
                createProjectListener(config)
            )
        }
    }

    private fun createDefaultListener(no: Int): RabbitListenerEndpoint {
        val queue = getDefaultQueueName(no)
        return SimpleRabbitListenerEndpoint().configure(queue)
    }

    private fun createProjectListener(config: String): RabbitListenerEndpoint {
        val queue = getProjectQueueName(config)
        return SimpleRabbitListenerEndpoint().configure(queue)
    }

    private fun getProjectQueueName(config: String): String =
        "${AsyncAutoVersioningQueueConfig.QUEUE_PREFIX}.${AsyncAutoVersioningQueueConfig.PROJECT}.$config"

    private fun getDefaultQueueName(no: Int) =
        "${AsyncAutoVersioningQueueConfig.QUEUE_PREFIX}.${AsyncAutoVersioningQueueConfig.DEFAULT}.$no"

    private fun SimpleRabbitListenerEndpoint.configure(
        queue: String,
    ): SimpleRabbitListenerEndpoint {
        id = queue
        setQueueNames(queue)
        concurrency = "1-1" // No concurrency, we want the events to be processed in turn
        messageListener = AVMessageListener()
        return this
    }

    private inner class AVMessageListener : ChannelAwareMessageListener {

        override fun onMessage(message: Message, channel: Channel?) {
            var order: AutoVersioningOrder? = null
            try {
                order = parseMessage(message)
                process(order, message)
            } catch (e: Exception) {
                onError(e, order)
            } finally {
                onEndMessage(message, channel)
            }
        }

        private fun onError(any: Exception, order: AutoVersioningOrder?) {
            metrics.onProcessingError()
            val root = ExceptionUtils.getRootCause(any)
            try {
                // Audit in the order
                order?.let {
                    autoVersioningAuditService.onError(it, root)
                }
            } finally {
                applicationLogService.log(
                    ApplicationLogEntry.error(
                        any,
                        NameDescription.nd("auto-versioning-error", "Auto versioning processing error"),
                        "Auto versioning could not be processed"
                    ).withDetail("message", any.message)
                )
            }
        }

        private fun onEndMessage(message: Message, channel: Channel?) {
            if (channel != null) {
                // Always acknowledge the message to prevent re-delivery
                try {
                    channel.basicAck(message.messageProperties.deliveryTag, false)
                } catch (e: IOException) {
                    // Log failing to acknowledge the message
                    logAckError(e)
                }
            }
        }

        private fun logAckError(e: IOException) {
            applicationLogService.log(
                ApplicationLogEntry.error(
                    e,
                    NameDescription.nd("auto-versioning-error", "Auto versioning message could not be acked."),
                    "Auto versioning could not be processed: ${e.message}"
                ).withDetail("message", e.message)
            )
        }

        private fun parseMessage(message: Message): AutoVersioningOrder {
            val body = message.body.toString(Charsets.UTF_8)
            return body.parseAsJson().parse<AutoVersioningOrder>()
        }

        private fun process(order: AutoVersioningOrder, message: Message) {
            // Gets the current state of the order
            val entry = autoVersioningAuditQueryService.findByUUID(order.branch, order.uuid)
            if (entry == null) {
                error("No audit entry found upon receiving the processing order")
            } else if (!entry.mostRecentState.state.isRunning) {
                logger.debug("Cancelled order, not processing. {}", entry)
                return
            }

            val queue = message.messageProperties.consumerQueue
            metrics.onReceiving(order, queue)
            securityService.asAdmin {
                autoVersioningAuditService.onReceived(order, queue)
                val outcome = metrics.processingTiming(order, queue) {
                    autoVersioningProcessingService.process(order)
                }
                metrics.onProcessingCompleted(order, outcome)
            }
        }
    }
}
