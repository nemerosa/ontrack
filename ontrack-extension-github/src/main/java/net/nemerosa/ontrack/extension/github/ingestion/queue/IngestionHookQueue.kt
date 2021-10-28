package net.nemerosa.ontrack.extension.github.ingestion.queue

import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload

/**
 * Service to post ingestion payloads on the queue.
 */
interface IngestionHookQueue {

    /**
     * Puts a payload on the queue.
     */
    fun queue(payload: IngestionHookPayload)

}