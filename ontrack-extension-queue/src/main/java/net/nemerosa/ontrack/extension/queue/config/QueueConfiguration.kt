package net.nemerosa.ontrack.extension.queue.config

import net.nemerosa.ontrack.extension.queue.QueueConfigProperties
import net.nemerosa.ontrack.extension.queue.QueueProcessor
import net.nemerosa.ontrack.extension.queue.queueNamePrefix
import net.nemerosa.ontrack.extension.queue.queueRoutingPrefix
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
        val topic = processor.queueNamePrefix
        val exchange = DirectExchange(topic)
        declarables += exchange

        // Default queues
        val prefix = processor.queueNamePrefix
        val scale = queueConfigProperties.specific[id]?.scale ?: 1
        (0 until scale).forEach { index ->
            val queue = Queue("$prefix.$index", true)
            val binding = BindingBuilder
                    .bind(queue)
                    .to(exchange)
                    .with("${processor.queueRoutingPrefix}.$index")
            declarables += queue
            declarables += binding
        }

        // Specific queues
        processor.specificConfiguration(exchange, declarables)
    }

}