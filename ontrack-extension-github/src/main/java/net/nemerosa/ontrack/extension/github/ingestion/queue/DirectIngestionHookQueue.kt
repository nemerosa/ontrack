package net.nemerosa.ontrack.extension.github.ingestion.queue

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.extension.github.ingestion.metrics.IngestionMetrics
import net.nemerosa.ontrack.extension.github.ingestion.metrics.increment
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload
import org.springframework.stereotype.Component

/**
 * Synchronous processing.
 */
@Component
@Deprecated("Use RabbitMQ implementation")
class DirectIngestionHookQueue(
    private val subscriber: IngestionHookQueueSubscriber,
    private val meterRegistry: MeterRegistry,
) : IngestionHookQueue {
    override fun queue(payload: IngestionHookPayload) {
        meterRegistry.increment(payload, IngestionMetrics.RECEIVED_COUNT)
        subscriber.onIngestionHookPayload(payload)
    }
}