package net.nemerosa.ontrack.extension.github.ingestion.processing.push

/**
 * Defines a service to process a push event.
 */
interface PushPayloadListener {
    /**
     * Processes the payload.
     *
     * @param payload Payload to process
     * @return Outcome of the processing
     */
    fun process(payload: PushPayload): PushPayloadListenerOutcome
}