package net.nemerosa.ontrack.extension.github.ingestion.processing

import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload

interface IngestionEventProcessor {

    /**
     * The ID of the event to process
     */
    val event: String

    /**
     * Processes the payload
     *
     * @param payload Payload to process
     * @return Outcome of the processing
     */
    fun process(payload: IngestionHookPayload): IngestionEventProcessingResult

}