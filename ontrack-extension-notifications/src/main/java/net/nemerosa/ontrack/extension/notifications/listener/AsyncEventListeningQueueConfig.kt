package net.nemerosa.ontrack.extension.notifications.listener

import org.springframework.amqp.core.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration of the queue(s) for the async processing of event dispatching.
 */
@Configuration
class AsyncEventListeningQueueConfig {

    @Bean
    fun notificationsDispatchingTopicBindings(): Declarables {
        val declarables = mutableListOf<Declarable>()
        // Topic exchange for the notifications
        val exchange = DirectExchange(TOPIC).apply {
            declarables += this
        }
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
         * Topic exchange name
         */
        const val TOPIC = "notifications.dispatching"

        /**
         * Prefix for the queue names
         */
        const val QUEUE_PREFIX = TOPIC

        /**
         * Default queuing
         */
        const val DEFAULT = "default"

        /**
         * Getting the routing key for a given payload
         */
        const val ROUTING_KEY = DEFAULT
    }
}