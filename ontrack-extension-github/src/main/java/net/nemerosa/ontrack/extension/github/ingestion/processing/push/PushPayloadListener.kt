package net.nemerosa.ontrack.extension.github.ingestion.processing.push

import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResultDetails

/**
 * Defines a service to process a push event.
 */
interface PushPayloadListener {

    /**
     * Checks if the payload must be processed.
     *
     * @param payload Payload to check
     * @return Result of the check
     */
    fun preProcessCheck(payload: PushPayload): PushPayloadListenerCheck

    /**
     * Processes the payload.
     *
     * @param payload Payload to process
     * @param configuration GitHub config name
     * @return Result of the processing
     */
    fun process(payload: PushPayload, configuration: String?): IngestionEventProcessingResultDetails
}