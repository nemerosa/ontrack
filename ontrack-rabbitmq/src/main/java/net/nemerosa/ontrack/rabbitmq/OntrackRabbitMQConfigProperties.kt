package net.nemerosa.ontrack.rabbitmq

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * Specific static configuration for RabbitMQ.
 */
@Component
@ConfigurationProperties(prefix = OntrackRabbitMQConfigProperties.PREFIX)
class OntrackRabbitMQConfigProperties {

    /**
     * True (default) to make the sending of messages part of the current transaction.
     */
    var transactional = true

    companion object {
        /**
         * Prefix for the properties
         */
        const val PREFIX = "ontrack.config.rabbitmq"
    }

}