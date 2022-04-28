package net.nemerosa.ontrack.extension.github.ingestion.queue

import net.nemerosa.ontrack.extension.github.ingestion.IngestionConfigProperties
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import org.springframework.amqp.core.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration of the queues.
 */
@Configuration
class AsyncIngestionHookQueueConfig(
    private val ingestionConfigProperties: IngestionConfigProperties,
) {

    @Bean
    fun gitHubIngestionTopicBindings(): Declarables {
        val declarables = mutableListOf<Declarable>()
        // Topic exchange for the ingestion
        val exchange = DirectExchange(TOPIC).apply {
            declarables += this
        }
        // Repository-specific queues
        ingestionConfigProperties.processing.repositories.forEach { (name, _) ->
            // The queue
            val queue = Queue("$QUEUE_PREFIX.$name")
            // The binding
            val binding = BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(name)
            // Adding them all
            declarables += queue
            declarables += binding
        }
        // Default queues
        if (ingestionConfigProperties.processing.scale > 1) {
            (1..ingestionConfigProperties.processing.scale).forEach { no ->
                val index = no - 1 // Starting at 0
                val queue = Queue("$QUEUE_PREFIX.$DEFAULT.$index", true)
                val binding = BindingBuilder
                    .bind(queue)
                    .to(exchange)
                    .with("$DEFAULT.$index")
                declarables += queue
                declarables += binding
            }
        } else {
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
        }
        // OK
        return Declarables(declarables)
    }

    companion object {

        /**
         * Getting the routing key for a given payload
         */
        fun getRoutingKey(ingestionConfigProperties: IngestionConfigProperties, repository: Repository?): String =
            getRepositorySpecificRoutingKey(ingestionConfigProperties, repository)
                ?: if (ingestionConfigProperties.processing.scale > 1) {
                    getRepositoryDefaultRoutingKey(ingestionConfigProperties, repository)
                } else {
                    DEFAULT
                }

        /**
         * Computing the specific queue to use for a given repository name
         */
        private fun getRepositorySpecificRoutingKey(
            ingestionConfigProperties: IngestionConfigProperties,
            repository: Repository?,
        ): String? = repository?.let {
            ingestionConfigProperties.processing.repositories.entries.find { (name, config) ->
                config.matches(owner = repository.owner.login, repository = repository.name)
            }?.key
        }

        /**
         * Computing the index of the default queue to use for a given repository name
         */
        private fun getRepositoryDefaultRoutingKey(
            ingestionConfigProperties: IngestionConfigProperties,
            repository: Repository?,
        ): String =
            if (repository != null) {
                val code = repository.fullName.hashCode() % ingestionConfigProperties.processing.scale
                "$DEFAULT.$code"
            } else {
                "$DEFAULT.0"
            }

        /**
         * Topic exchange name
         */
        const val TOPIC = "github.ingestion"

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