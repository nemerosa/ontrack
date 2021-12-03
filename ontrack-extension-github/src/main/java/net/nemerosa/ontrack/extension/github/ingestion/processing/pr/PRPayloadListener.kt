package net.nemerosa.ontrack.extension.github.ingestion.processing.pr

/**
 * Defines a service which listens to `pull_request` events.
 */
interface PRPayloadListener {

    /**
     * Checks if the payload must be processed.
     *
     * @param payload Payload to check
     * @return Result of the check
     */
    fun preProcessCheck(payload: PRPayload): PRPayloadListenerCheck

    /**
     * Processes the payload.
     *
     * @param payload Payload to process
     * @param configuration GitHub config name
     */
    fun process(payload: PRPayload, configuration: String?)
}