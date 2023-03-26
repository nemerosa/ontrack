package net.nemerosa.ontrack.extension.queue.config

import net.nemerosa.ontrack.extension.queue.QueueConfigProperties
import net.nemerosa.ontrack.extension.queue.QueueProcessor
import org.springframework.amqp.core.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class QueueConfiguration(
    private val queueConfigProperties: QueueConfigProperties,
    private val queueProcessors: List<QueueProcessor<*>>,
) {

    @Bean
    fun queueConfigurationDeclarables(): Declarables {
        val declarables = mutableListOf<Declarable>()
        // For all processors
        queueProcessors.forEach { processor ->
            declareQueueProcessor(declarables, processor)
        }
        // OK
        return Declarables(declarables)
    }

    private fun declareQueueProcessor(
        declarables: MutableList<Declarable>,
        processor: QueueProcessor<*>
    ) {
        val id = processor.id
        // Topic
        val topic = "ontrack.queue.$id.topic"
        val exchange = DirectExchange(topic)
        declarables += exchange

        // Default queues
        val prefix = "ontrack.queue.$id"
        val scale = queueConfigProperties.specific[id]?.scale ?: 1
        if (scale > 1) {
            (1..scale).forEach { no ->
                val index = no - 1 // Starting at 0
                val queue = Queue("$prefix.$index", true)
                val binding = BindingBuilder
                    .bind(queue)
                    .to(exchange)
                    .with("$prefix.routing.$index")
                declarables += queue
                declarables += binding
            }
        } else {
            val defaultQueue = Queue("$prefix.0", true).apply {
                declarables += this
            }
            // Catch-all binding
            declarables += BindingBuilder
                .bind(defaultQueue)
                .to(exchange)
                .with("$prefix.routing.0")
        }
    }

}