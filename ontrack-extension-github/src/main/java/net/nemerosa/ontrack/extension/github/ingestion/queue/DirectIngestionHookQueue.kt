package net.nemerosa.ontrack.extension.github.ingestion.queue

import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload
import org.springframework.stereotype.Component

/**
 * Synchronous processing.
 */
@Component
@Deprecated("Use RabbitMQ implementation")
class DirectIngestionHookQueue(
    private val subscriber: IngestionHookQueueSubscriber,
) : IngestionHookQueue {
    override fun queue(payload: IngestionHookPayload) {
        subscriber.onIngestionHookPayload(payload)
    }
}