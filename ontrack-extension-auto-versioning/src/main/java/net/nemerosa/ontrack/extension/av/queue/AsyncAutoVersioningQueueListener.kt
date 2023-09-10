package net.nemerosa.ontrack.extension.av.queue

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
import org.springframework.amqp.core.MessageListener
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpoint
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar
import org.springframework.stereotype.Component
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

    private val listener = MessageListener(::onMessage)

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
        messageListener = listener
        return this
    }

    private fun onMessage(message: Message) {
        val body = message.body.toString(Charsets.UTF_8)
        var order: AutoVersioningOrder? = null
        try {
            order = body.parseAsJson().parse<AutoVersioningOrder>()

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
        } catch (any: Throwable) {
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
                        "Auto versioning could not be processed: $body"
                    ).withDetail("message", any.message)
                )
            }
        }
    }
}
