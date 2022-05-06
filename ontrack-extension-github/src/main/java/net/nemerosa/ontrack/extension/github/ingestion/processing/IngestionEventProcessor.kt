package net.nemerosa.ontrack.extension.github.ingestion.processing

import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload

interface IngestionEventProcessor {

    /**
     * The ID of the event to process
     */
    val event: String

    /**
     * Pre-processing check, checking if the payload must be processed or not.
     *
     * @param payload Payload to check
     * @return Result of the check
     */
    fun preProcessingCheck(payload: IngestionHookPayload): IngestionEventPreprocessingCheck

    /**
     * Processes the payload
     *
     * @param payload Payload to process
     * @return Outcome of the processing
     */
    fun process(payload: IngestionHookPayload): IngestionEventProcessingResult

    /**
     * Computes a display string describing the payload.
     */
    fun getPayloadSource(payload: IngestionHookPayload): String?

}