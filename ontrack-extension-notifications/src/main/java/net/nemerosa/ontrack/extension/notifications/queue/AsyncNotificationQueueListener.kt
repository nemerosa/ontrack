package net.nemerosa.ontrack.extension.notifications.queue

import com.rabbitmq.client.Channel
import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.extension.notifications.NotificationsConfigProperties
import net.nemerosa.ontrack.extension.notifications.metrics.NotificationsMetrics
import net.nemerosa.ontrack.extension.notifications.model.Notification
import net.nemerosa.ontrack.extension.notifications.processing.NotificationProcessingService
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.events.SerializableEventService
import net.nemerosa.ontrack.model.metrics.increment
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

@Component
class AsyncNotificationQueueListener(
    private val securityService: SecurityService,
    private val notificationProcessingService: NotificationProcessingService,
    private val applicationLogService: ApplicationLogService,
    private val meterRegistry: MeterRegistry,
    private val notificationsConfigProperties: NotificationsConfigProperties,
    private val accountService: AccountService,
    private val serializableEventService: SerializableEventService,
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

    private val listener = ChannelAwareMessageListener(::onMessage)

    private fun SimpleRabbitListenerEndpoint.configure(
        queue: String,
    ): SimpleRabbitListenerEndpoint {
        id = queue
        setQueueNames(queue)
        concurrency = "1-${notificationsConfigProperties.processing.queue.concurrency}"
        messageListener = listener
        return this
    }

    private fun onMessage(message: Message, channel: Channel?) {
        try {
            val body = message.body.toString(Charsets.UTF_8)
            val payload = body.parseAsJson().parse<NotificationQueuePayload>()

            // Gets the account to use from the queue payload
            val account = securityService.asAdmin {
                accountService.findAccountByName(payload.accountName)
                    ?: error("Notification message received without authentication")
            }

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

            SecurityContextHolder.setContext(securityContext)
            try {
                // Extracts the notification from the payload
                val notification = Notification(
                    source = payload.source,
                    channel = payload.channel,
                    channelConfig = payload.channelConfig,
                    event = serializableEventService.hydrate(payload.serializableEvent),
                    template = payload.template,
                )
                val queue = message.messageProperties.consumerQueue
                meterRegistry.increment(
                    NotificationsMetrics.event_dispatching_dequeued,
                    "event" to notification.event.eventType.id,
                    "channel" to notification.channel,
                    "queue" to queue,
                )
                notificationProcessingService.process(notification, emptyMap()) {}
            } finally {
                SecurityContextHolder.setContext(oldSecurityContext)
            }
        } catch (any: Throwable) {
            applicationLogService.log(
                ApplicationLogEntry.error(
                    any,
                    NameDescription.nd("notifications-processing-error", "Catch-all error in notifications processing"),
                    "Uncaught error during the notifications processing"
                )
            )
        }
    }

}