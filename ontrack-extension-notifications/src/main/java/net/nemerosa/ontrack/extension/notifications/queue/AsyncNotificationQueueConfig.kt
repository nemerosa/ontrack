package net.nemerosa.ontrack.extension.notifications.queue

import net.nemerosa.ontrack.extension.notifications.NotificationsConfigProperties
import net.nemerosa.ontrack.extension.notifications.model.Notification
import org.springframework.amqp.core.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration of the queue(s) for the async processing of event notifications.
 */
@Configuration
class AsyncNotificationQueueConfig(
    // private val notificationsConfigProperties: NotificationsConfigProperties,
) {

    @Bean
    fun notificationsTopicBindings(): Declarables {
        val declarables = mutableListOf<Declarable>()
        // Topic exchange for the notifications
        val exchange = DirectExchange(TOPIC).apply {
            declarables += this
        }
        // TODO Channel-specific queues
        // Default queue (catch-all)
        val defaultQueue = Queue(
            "$QUEUE_PREFIX.$DEFAULT",
            true, // We keep the queue alive for reprocessing
        ).apply {
            declarables += this
        }
        // Catch-all binding
        declarables += BindingBuilder
            .bind(defaultQueue)
            .to(exchange)
            .with(DEFAULT)
        // OK
        return Declarables(declarables)
    }

    companion object {

        /**
         * Getting the routing key for a given payload
         *
         * TODO Channel-specific queues
         */
        fun getRoutingKey(
            notificationsConfigProperties: NotificationsConfigProperties,
            item: Notification,
        ): String =
            DEFAULT

        /**
         * Topic exchange name
         */
        const val TOPIC = "notifications"

        /**
         * Prefix for the queue names
         */
        const val QUEUE_PREFIX = TOPIC

        /**
         * Default queuing
         */
        const val DEFAULT = "default"
    }
}