package net.nemerosa.ontrack.extension.av.queue

import net.nemerosa.ontrack.extension.av.AutoVersioningConfigProperties
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import org.springframework.amqp.core.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration of the queues.
 */
@Configuration
class AsyncAutoVersioningQueueConfig(
    private val autoVersioningConfigProperties: AutoVersioningConfigProperties,
) {

    @Bean
    fun autoVersioningTopicBindings(): Declarables {
        val declarables = mutableListOf<Declarable>()
        // Topic exchange for the auto versioning
        val exchange = DirectExchange(TOPIC).apply {
            declarables += this
        }
        // Default queues
        (1..autoVersioningConfigProperties.queue.scale).forEach { no ->
            val queue = Queue("$QUEUE_PREFIX.$DEFAULT.$no", true)
            val binding = BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("$DEFAULT.$no")
            declarables += queue
            declarables += binding
        }
        // OK
        return Declarables(declarables)
    }

    companion object {

        /**
         * Getting the routing key for a given payload
         */
        fun getRoutingKey(
            autoVersioningConfigProperties: AutoVersioningConfigProperties,
            order: AutoVersioningOrder,
        ): String = "$DEFAULT.${(order.branch.id() % autoVersioningConfigProperties.queue.scale) + 1}"

        /**
         * Topic exchange name
         */
        const val TOPIC = "auto-versioning"

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