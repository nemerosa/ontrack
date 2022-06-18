package net.nemerosa.ontrack.rabbitmq

import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct


/**
 * Enabling Rabbit MQ
 */
@Configuration
@EnableRabbit
class OntrackRabbitMQConfig(
    private val rabbitTemplate: RabbitTemplate,
    private val properties: OntrackRabbitMQConfigProperties,
) {

    @PostConstruct
    protected fun init() {
        // make rabbit template to support transactions
        rabbitTemplate.isChannelTransacted = properties.transactional
    }

}
