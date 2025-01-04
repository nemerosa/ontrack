package net.nemerosa.ontrack.rabbitmq

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * Specific static configuration for RabbitMQ.
 */
@Component
@ConfigurationProperties(prefix = OntrackRabbitMQConfigProperties.PREFIX)
@APIName("RabbitMQ configuration")
@APIDescription("Configuration of the client from Ontrack to Rabbit MQ. Note that basic connection parameters are handled using Spring Boot configuration.")
class OntrackRabbitMQConfigProperties {

    @APIDescription("True (default) to make the sending of messages part of the current transaction.")
    var transactional = true

    companion object {
        /**
         * Prefix for the properties
         */
        const val PREFIX = "ontrack.config.rabbitmq"
    }

}