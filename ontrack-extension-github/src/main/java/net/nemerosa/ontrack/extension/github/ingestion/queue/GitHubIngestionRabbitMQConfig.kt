package net.nemerosa.ontrack.extension.github.ingestion.queue

import net.nemerosa.ontrack.extension.github.ingestion.IngestionConfigProperties
import org.springframework.amqp.core.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration of the queues.
 */
@Configuration
class GitHubIngestionRabbitMQConfig(
    private val ingestionConfigProperties: IngestionConfigProperties,
) {

    @Bean
    fun gitHubIngestionTopicBindings(): Declarables {
        val declarables = mutableListOf<Declarable>()
        // TODO Gets the queues & bindings from the configuration properties
        val defaultQueue = Queue(
            "$QUEUE_PREFIX.default",
            true, // We keep the queue alive for reprocessing
        ).apply {
            declarables += this
        }
        // Topic exchange for the ingestion
        val ingestionTopicExchange = TopicExchange(TOPIC).apply {
            declarables += this
        }
        // Bindings
        // TODO Bindings for specific repositories
        // Catch-all binding
        declarables += BindingBuilder
            .bind(defaultQueue)
            .to(ingestionTopicExchange)
            .with(ROUTING_CATCH_ALL)
        // OK
        return Declarables(declarables)
    }

    companion object {
        /**
         * Topic exchange name
         */
        const val TOPIC = "github.ingestion"
        /**
         * Prefix for the queue names
         */
        const val QUEUE_PREFIX = TOPIC

        /**
         * Prefix for the queue names
         */
        const val ROUTING_CATCH_ALL = "#"
    }

}