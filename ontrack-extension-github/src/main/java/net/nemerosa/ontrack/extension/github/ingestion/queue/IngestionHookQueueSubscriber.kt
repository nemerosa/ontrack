package net.nemerosa.ontrack.extension.github.ingestion.queue

import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload

/**
 * Registers to events on the queue
 */
@Deprecated("Use RabbitMQ integration")
interface IngestionHookQueueSubscriber {

    /**
     * Reacts on a received payload
     */
    fun onIngestionHookPayload(payload: IngestionHookPayload)

}