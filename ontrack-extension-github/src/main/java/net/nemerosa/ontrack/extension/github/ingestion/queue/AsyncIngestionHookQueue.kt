package net.nemerosa.ontrack.extension.github.ingestion.queue

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.extension.github.ingestion.IngestionConfigProperties
import net.nemerosa.ontrack.extension.github.ingestion.metrics.INGESTION_METRIC_ROUTING_TAG
import net.nemerosa.ontrack.extension.github.ingestion.metrics.IngestionMetrics
import net.nemerosa.ontrack.extension.github.ingestion.metrics.increment
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayloadStorage
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.format
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Asynchronous processing, used for production.
 */
@Component
@ConditionalOnProperty(
    prefix = "ontrack.extension.github.ingestion.processing",
    name = ["async"],
    havingValue = "true",
    matchIfMissing = true,
)
class AsyncIngestionHookQueue(
    private val meterRegistry: MeterRegistry,
    private val amqpTemplate: AmqpTemplate,
    private val ingestionHookPayloadStorage: IngestionHookPayloadStorage,
    private val ingestionConfigProperties: IngestionConfigProperties,
    private val securityService: SecurityService,
) : IngestionHookQueue {
    override fun queue(payload: IngestionHookPayload) {
        val routingKey = AsyncIngestionHookQueueConfig.getRoutingKey(ingestionConfigProperties, payload.repository)
        ingestionHookPayloadStorage.routing(payload, routingKey)
        meterRegistry.increment(payload, IngestionMetrics.Queue.producedCount, INGESTION_METRIC_ROUTING_TAG to routingKey)
        val message = payload.asJson().format()
        amqpTemplate.convertAndSend(
            AsyncIngestionHookQueueConfig.TOPIC,
            // Routing key = repository name or default
            routingKey,
            message,
        )
    }
}