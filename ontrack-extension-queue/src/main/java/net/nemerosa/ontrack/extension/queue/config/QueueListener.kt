package net.nemerosa.ontrack.extension.queue.config

import com.rabbitmq.client.Channel
import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.extension.queue.*
import net.nemerosa.ontrack.extension.queue.metrics.queueMessageReceived
import net.nemerosa.ontrack.extension.queue.metrics.queueProcessCompleted
import net.nemerosa.ontrack.extension.queue.metrics.queueProcessErrored
import net.nemerosa.ontrack.extension.queue.metrics.queueProcessTime
import net.nemerosa.ontrack.extension.queue.record.QueueRecordService
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.security.AccountOntrackUser
import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.support.ApplicationLogEntry
import net.nemerosa.ontrack.model.support.ApplicationLogService
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpoint
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.TransientSecurityContext
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class QueueListener(
    private val queueConfigProperties: QueueConfigProperties,
    private val queueProcessors: List<QueueProcessor<*>>,
    private val securityService: SecurityService,
    private val applicationLogService: ApplicationLogService,
    private val queueRecordService: QueueRecordService,
    private val meterRegistry: MeterRegistry,
    private val accountService: AccountService,
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
        val scale = queueConfigProperties.getQueueProcessorScale(queueProcessor)
        (0 until scale).forEach { index ->
            registrar.registerEndpoint(
                createDefaultListener(queueProcessor, index)
            )
        }
    }

    private fun createDefaultListener(
        queueProcessor: QueueProcessor<*>,
        index: Int,
    ): RabbitListenerEndpoint {
        val queue = "${queueProcessor.queueNamePrefix}.$index"
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
    ) : ChannelAwareMessageListener {

        override fun onMessage(message: Message, channel: Channel?) {

            if (queueProcessor.ackMode == QueueAckMode.IMMEDIATE) {
                ackMessage(message, channel)
            }

            try {
                val queue = message.messageProperties.consumerQueue
                val body = message.body.toString(Charsets.UTF_8).parseAsJson()
                val qp = QueuePayload.parse(body)
                meterRegistry.queueMessageReceived(qp)
                queueRecordService.received(qp, queue)

                // Checks the processor
                if (qp.processor != queueProcessor.id) {
                    throw IllegalStateException("Queue processor ${queueProcessor.id} received message for ${qp.processor}...")
                }

                // Parsing the payload
                val payload = qp.parse(queueProcessor.payloadType)
                queueRecordService.parsed(qp, payload)

                // Check for processing
                val cancelReason = queueProcessor.isCancelled(payload)
                if (cancelReason != null) {
                    queueRecordService.cancelled(qp, cancelReason)
                    return
                }

                // Processing
                securityService.asAdmin {
                    queueRecordService.processing(qp)

                    // Gets the account to use from the queue payload
                    val account = accountService.findAccountByName(qp.accountName)
                        ?: throw QueuePayloadAccountNameNotFoundException(qp)

                    // Sets the security context for expected account
                    val user = AccountOntrackUser(account)
                    val authenticatedUser = accountService.withACL(user)
                    val authentication = UsernamePasswordAuthenticationToken(
                        authenticatedUser,
                        "",
                        user.authorities
                    )
                    val oldSecurityContext = SecurityContextHolder.getContext()
                    val securityContext = TransientSecurityContext(authentication)

                    try {
                        meterRegistry.queueProcessTime(qp) {
                            try {
                                SecurityContextHolder.setContext(securityContext)
                                queueProcessor.process(payload)
                            } finally {
                                SecurityContextHolder.setContext(oldSecurityContext)
                            }
                        }
                        meterRegistry.queueProcessCompleted(qp)
                        queueRecordService.completed(qp)
                    } catch (any: Exception) {
                        meterRegistry.queueProcessErrored(qp)
                        queueRecordService.errored(qp, any)
                        throw any
                    }
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
            } finally {
                if (queueProcessor.ackMode == QueueAckMode.END) {
                    ackMessage(message, channel)
                }
            }
        }

        private fun ackMessage(message: Message, channel: Channel?) {
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
                    NameDescription.nd("queue-ack-error", "Message could not be acked."),
                    "Message could not be acked: ${e.message}"
                ).withDetail("message", e.message)
                    .withDetail("queue.processor", queueProcessor.id)
            )
        }

    }

}

