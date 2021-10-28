package net.nemerosa.ontrack.queue.config

import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.context.annotation.Configuration

/**
 * Enabling queuing in Ontrack & configuring the queues.
 */
@Configuration
@EnableRabbit
class QueueConfiguration {
}