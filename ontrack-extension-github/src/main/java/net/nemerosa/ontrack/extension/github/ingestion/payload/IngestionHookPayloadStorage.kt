package net.nemerosa.ontrack.extension.github.ingestion.payload

import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResult
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
     * Routing information
     *
     * @param payload Payload to save
     * @param routing Routing information to save
     */
    fun routing(payload: IngestionHookPayload, routing: String)

    /**
     * Queue information
     *
     * @param payload Payload to save
     * @param queue Queue information to save
     */
    fun queue(payload: IngestionHookPayload, queue: String)

    /**
     * Adapt the status for a payload's processing starting
     */
    fun start(payload: IngestionHookPayload)

    /**
     * Adapt the status for a payload's processing success.
     */
    fun finished(payload: IngestionHookPayload, outcome: IngestionEventProcessingResult)

    /**
     * Adapt the status for a payload's processing in error
     */
    fun error(payload: IngestionHookPayload, any: Throwable)

    /**
     * Number of stored payloads
     *
     * @param statuses List of statuses to filter on. Empty or null to get them all.
     * @param outcome Outcome of the processing
     * @param gitHubDelivery Filter on the GitHub Delivery ID
     * @param gitHubEvent Filter on the GitHub Event
     * @param repository Filter on the repository
     * @param owner Filter on the repository owner
     * @param routing Filter on the routing information
     * @param queue Filter on the queue information
     */
    fun count(
        statuses: List<IngestionHookPayloadStatus>? = null,
        outcome: IngestionEventProcessingResult? = null,
        gitHubDelivery: String? = null,
        gitHubEvent: String? = null,
        repository: String? = null,
        owner: String? = null,
        routing: String? = null,
        queue: String? = null,
    ): Int

    /**
     * Gets a list of stored payloads.
     *
     * @param offset Start of the page
     * @param size Maximum of items in a page
     * @param statuses List of statuses to filter on. Empty or null to get them all.
     * @param outcome Outcome of the processing
     * @param gitHubDelivery Filter on the GitHub Delivery ID
     * @param gitHubEvent Filter on the GitHub Event
     * @param repository Filter on the repository
     * @param owner Filter on the repository owner
     * @param routing Filter on the routing information
     * @param queue Filter on the queue information
     * @return List of matching payloads
     */
    fun list(
        offset: Int = 0,
        size: Int = 40,
        statuses: List<IngestionHookPayloadStatus>? = null,
        outcome: IngestionEventProcessingResult? = null,
        gitHubDelivery: String? = null,
        gitHubEvent: String? = null,
        repository: String? = null,
        owner: String? = null,
        routing: String? = null,
        queue: String? = null,
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