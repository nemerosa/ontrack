package net.nemerosa.ontrack.extension.github.ingestion.payload

/**
 * Management of the hook payloads.
 */
interface IngestionHookPayloadStorage {

    /**
     * Stores the payload for traceability
     */
    fun store(payload: IngestionHookPayload)

}