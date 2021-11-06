package net.nemerosa.ontrack.extension.github.ingestion.payload

import java.time.LocalDateTime

/**
 * Management of the hook payloads.
 */
interface IngestionHookPayloadStorage {

    /**
     * Stores the payload for traceability
     */
    fun store(payload: IngestionHookPayload)

    /**
     * Adapt the status for a payload's processing starting
     */
    fun start(payload: IngestionHookPayload)

    /**
     * Adapt the status for a payload's processing successig
     */
    fun finished(payload: IngestionHookPayload)

    /**
     * Adapt the status for a payload's processing in error
     */
    fun error(payload: IngestionHookPayload, any: Throwable)

    /**
     * Number of stored payloads
     *
     * @param statuses List of statuses to filter on. Empty or null to get them all.
     */
    fun count(
        statuses: List<IngestionHookPayloadStatus>? = null,
    ): Int

    /**
     * Gets a list of stored payloads.
     *
     * @param offset Start of the page
     * @param size Maximum of items in a page
     * @param statuses List of statuses to filter on. Empty or null to get them all.
     * @return List of matching payloads
     */
    fun list(
        offset: Int = 0,
        size: Int = 40,
        statuses: List<IngestionHookPayloadStatus>? = null,
    ): List<IngestionHookPayload>

    /**
     * Removes all payloads that are older than the [until] date.
     *
     * @return Number of items which have been deleted.
     */
    fun cleanUntil(until: LocalDateTime): Int

    /**
     * Gets a payload using its UUID
     */
    fun findByUUID(uuid: String): IngestionHookPayload?

}