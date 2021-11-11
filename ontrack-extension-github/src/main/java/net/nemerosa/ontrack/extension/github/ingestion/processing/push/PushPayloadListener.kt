package net.nemerosa.ontrack.extension.github.ingestion.processing.push

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
     */
    fun process(payload: PushPayload)
}