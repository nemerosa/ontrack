package net.nemerosa.ontrack.extension.github.ingestion.processing

import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload

interface IngestionEventProcessor {

    /**
     * The ID of the event to process
     */
    val event: String

    /**
     * Processes the payload
     */
    fun process(payload: IngestionHookPayload)

}