package net.nemerosa.ontrack.extension.github.ingestion

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * Static configuration for the ingestion.
 *
 * @property processing Queue configuration
 */
@Component
@ConfigurationProperties(prefix = IngestionConfigProperties.PREFIX)
class IngestionConfigProperties(
    var processing: ProcessingConfig = ProcessingConfig(),
) {
    /**
     * Processing configuration
     *
     * @property async Behaviour of the processing. By default, true, using a RabbitMQ engine
     * @property default Configuration for the default queue.
     * @property repositories List of specific bindings
     */
    class ProcessingConfig(
        var async: Boolean = true,
        var default: QueueConfig = QueueConfig(),
        var repositories: Map<String, RepositoryQueueConfig> = emptyMap(),
    )

    /**
     * Queue configuration for a repository and/or organization
     *
     * @property owner Regex for the repository owner, null for match all
     * @property repository Regex for the repository name, null for match all
     * @property config Configuration to use
     */
    class RepositoryQueueConfig(
        var owner: String? = null,
        var repository: String? = null,
        var config: QueueConfig = QueueConfig(),
    ) {
        fun matches(owner: String, repository: String) =
            matching(this.owner?.toRegex(), owner) && matching(this.repository?.toRegex(), repository)

        private fun matching(regex: Regex?, value: String): Boolean =
            regex == null || regex.matches(value)
    }

    /**
     * Queue configuration
     *
     * @property concurrency Maximum concurrency for a queue
     */
    class QueueConfig(
        var concurrency: UInt = DEFAULT_CONCURRENCY,
    )

    companion object {
        /**
         * Prefix for the properties
         */
        const val PREFIX = "ontrack.extension.github.ingestion"

        /**
         * Default concurrency for a queue
         */
        const val DEFAULT_CONCURRENCY = 10U
    }
}