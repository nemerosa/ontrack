package net.nemerosa.ontrack.extension.github.ingestion

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * Static configuration for the ingestion.
 *
 * @property queue Queue configuration
 */
@Component
@ConfigurationProperties(prefix = IngestionConfigProperties.PREFIX)
class IngestionConfigProperties(
    var queue: QueueConfig = QueueConfig(),
) {
    /**
     * Queue configuration type
     *
     * @property async Behaviour of the queuing. By default, true, using a RabbitMQ engine
     */
    class QueueConfig(
        var async: Boolean = true,
    )

    companion object {
        /**
         * Prefix for the properties
         */
        const val PREFIX = "ontrack.extension.github.ingestion"
    }
}