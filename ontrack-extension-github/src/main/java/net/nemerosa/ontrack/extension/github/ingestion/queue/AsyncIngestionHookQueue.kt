package net.nemerosa.ontrack.extension.github.ingestion.queue

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.extension.github.ingestion.metrics.IngestionMetrics
import net.nemerosa.ontrack.extension.github.ingestion.metrics.increment
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.format
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Asynchronous processing, used for production.
 */
@Component
@ConditionalOnProperty(
    prefix = "ontrack.extension.github.ingestion.queue",
    name = ["async"],
    havingValue = "true",
    matchIfMissing = true,
)
class AsyncIngestionHookQueue(
    private val meterRegistry: MeterRegistry,
    private val amqpTemplate: AmqpTemplate,
) : IngestionHookQueue {
    override fun queue(payload: IngestionHookPayload) {
        meterRegistry.increment(payload, IngestionMetrics.Queue.receivedCount)
        val message = payload.asJson().format()
        amqpTemplate.convertAndSend(
            GitHubIngestionRabbitMQConfig.TOPIC,
            // Routing key = repository name or default
            payload.repository?.fullName ?: GitHubIngestionRabbitMQConfig.DEFAULT,
            message,
        )
    }
}