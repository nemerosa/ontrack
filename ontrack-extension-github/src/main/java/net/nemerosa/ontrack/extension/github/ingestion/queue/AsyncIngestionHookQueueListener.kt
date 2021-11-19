package net.nemerosa.ontrack.extension.github.ingestion.queue

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.extension.github.ingestion.IngestionConfigProperties
import net.nemerosa.ontrack.extension.github.ingestion.metrics.INGESTION_METRIC_QUEUE_TAG
import net.nemerosa.ontrack.extension.github.ingestion.metrics.IngestionMetrics
import net.nemerosa.ontrack.extension.github.ingestion.metrics.increment
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayloadStorage
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionHookProcessingService
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.support.ApplicationLogEntry
import net.nemerosa.ontrack.model.support.ApplicationLogService
import org.springframework.amqp.core.Message
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
    private val ingestionHookPayloadStorage: IngestionHookPayloadStorage,
    private val securityService: SecurityService,
    private val applicationLogService: ApplicationLogService,
    private val meterRegistry: MeterRegistry,
) : RabbitListenerConfigurer {

    private val listener = MessageListener(::onMessage)

    override fun configureRabbitListeners(registrar: RabbitListenerEndpointRegistrar) {
        // Registers listeners for configured repositories
        ingestionConfigProperties.processing.repositories.forEach { (name, config) ->
            createSpecificListener(registrar, name, config)
        }
        // Listener for the default
        registrar.registerEndpoint(
            createDefaultListener(),
        )
    }

    private fun createSpecificListener(
        registrar: RabbitListenerEndpointRegistrar,
        name: String,
        config: IngestionConfigProperties.RepositoryQueueConfig
    ) {
        val queue = "${AsyncIngestionHookQueueConfig.QUEUE_PREFIX}.$name"
        val endpoint = SimpleRabbitListenerEndpoint().configure(
            queue,
            config.config
        )
        registrar.registerEndpoint(endpoint)
    }

    private fun createDefaultListener(): RabbitListenerEndpoint {
        val queue = "${AsyncIngestionHookQueueConfig.QUEUE_PREFIX}.${AsyncIngestionHookQueueConfig.DEFAULT}"
        return SimpleRabbitListenerEndpoint().configure(queue, ingestionConfigProperties.processing.default)
    }

    private fun SimpleRabbitListenerEndpoint.configure(
        queue: String,
        config: IngestionConfigProperties.QueueConfig,
    ): SimpleRabbitListenerEndpoint {
        id = queue
        setQueueNames(queue)
        val max = config.concurrency
        concurrency = "$max-$max"
        messageListener = listener
        return this
    }

    private fun onMessage(message: Message) {
        try {
            val body = message.body.toString(Charsets.UTF_8)
            val payload = body.parseAsJson().parse<IngestionHookPayload>()
            val queue = message.messageProperties.consumerQueue
            meterRegistry.increment(
                payload,
                IngestionMetrics.Queue.consumedCount,
                INGESTION_METRIC_QUEUE_TAG to queue
            )
            securityService.asAdmin {
                ingestionHookPayloadStorage.queue(payload, queue)
                ingestionHookProcessingService.process(payload)
            }
        } catch (any: Throwable) {
            applicationLogService.log(
                ApplicationLogEntry.error(
                    any,
                    NameDescription.nd("github-ingestion-error", "Catch-all error in GitHub ingestion processing"),
                    "Uncaught error during the GitHub ingestion processing"
                )
            )
        }
    }
}