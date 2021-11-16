package net.nemerosa.ontrack.extension.github.ingestion.queue

import net.nemerosa.ontrack.extension.github.ingestion.IngestionConfigProperties
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionHookProcessingService
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.json.parseAsJson
import org.springframework.amqp.core.MessageListener
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpoint
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar
import org.springframework.stereotype.Component

@Component
class AsyncIngestionHookQueueListener(
    private val ingestionConfigProperties: IngestionConfigProperties,
    private val ingestionHookProcessingService: IngestionHookProcessingService,
) : RabbitListenerConfigurer {

    private val listener = MessageListener { message ->
        onMessage(message.body.toString(Charsets.UTF_8))
    }

    override fun configureRabbitListeners(registrar: RabbitListenerEndpointRegistrar) {
        // TODO Register listeners for configured repositories
        // Listener for the default
        registrar.registerEndpoint(
            createDefaultListener(),
        )
    }

    private fun createDefaultListener(): RabbitListenerEndpoint {
        val queue = "${GitHubIngestionRabbitMQConfig.QUEUE_PREFIX}.${GitHubIngestionRabbitMQConfig.DEFAULT}"
        return SimpleRabbitListenerEndpoint().apply {
            id = queue
            setQueueNames(queue)
            // TODO concurrency = "..."
            messageListener = listener
        }
    }

    private fun onMessage(message: String) {
        val payload = message.parseAsJson().parse<IngestionHookPayload>()
        ingestionHookProcessingService.process(payload)
    }
}