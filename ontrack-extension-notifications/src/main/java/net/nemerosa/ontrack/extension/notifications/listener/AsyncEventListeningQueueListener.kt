package net.nemerosa.ontrack.extension.notifications.listener

import com.rabbitmq.client.Channel
import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.extension.notifications.metrics.NotificationsMetrics
import net.nemerosa.ontrack.extension.notifications.metrics.incrementForEvent
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.metrics.increment
import net.nemerosa.ontrack.model.security.AccountOntrackUser
import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.StructureService
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
class AsyncEventListeningQueueListener(
    private val eventFactory: EventFactory,
    private val structureService: StructureService,
    private val eventListeningService: EventListeningService,
    private val applicationLogService: ApplicationLogService,
    private val meterRegistry: MeterRegistry,
    private val securityService: SecurityService,
    private val accountService: AccountService,
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

    private val listener = ChannelAwareMessageListener(::onMessage)

    private fun SimpleRabbitListenerEndpoint.configure(
        queue: String,
    ): SimpleRabbitListenerEndpoint {
        id = queue
        setQueueNames(queue)
        concurrency = "1-1" // No concurrency, we want the events to be processed in turn
        messageListener = listener
        return this
    }

    private fun onMessage(message: Message, channel: Channel?) {
        try {
            val body = message.body.toString(Charsets.UTF_8)
            val payload = body.parseAsJson().parse<AsyncEventListeningQueueEvent>()

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

            try {
                SecurityContextHolder.setContext(securityContext)
                val event = payload.toEvent(eventFactory, structureService)
                meterRegistry.incrementForEvent(
                    NotificationsMetrics.event_listening_dequeued,
                    event
                )
                eventListeningService.onEvent(event)
            } finally {
                SecurityContextHolder.setContext(oldSecurityContext)
            }

        } catch (any: Throwable) {
            meterRegistry.increment(
                NotificationsMetrics.event_listening_dequeued_error
            )
            applicationLogService.log(
                ApplicationLogEntry.error(
                    any,
                    NameDescription.nd(
                        "notifications-dispatching-error",
                        "Catch-all error in notifications dispatching"
                    ),
                    "Uncaught error during the notifications dispatching"
                )
            )
        }
    }

}