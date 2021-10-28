package net.nemerosa.ontrack.extension.github.ingestion.payload

/**
 * Management of the hook payloads.
 */
interface IngestionHookPayloadStorage {

    /**
     * Stores the payload for traceability
     */
    fun store(payload: IngestionHookPayload)

    /**
     * Number of stored payloads
     */
    fun count(): Int

    /**
     * Gets a list of stored payloads.
     *
     * @param offset Start of the page
     * @param size Maximum of items in a page
     * @return List of matching payloads
     */
    fun list(
        offset: Int = 0,
        size: Int = 40,
    ): List<IngestionHookPayload>

}