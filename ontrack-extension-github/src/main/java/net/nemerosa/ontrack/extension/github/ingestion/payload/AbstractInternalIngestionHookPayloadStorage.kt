package net.nemerosa.ontrack.extension.github.ingestion.payload

import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResult
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.StorageService
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

abstract class AbstractInternalIngestionHookPayloadStorage(
    private val storageService: StorageService,
    securityService: SecurityService,
) : AbstractIngestionHookPayloadStorage(securityService) {

    override fun internalStore(payload: IngestionHookPayload) {
        storageService.store(
            INGESTION_HOOK_PAYLOAD_STORE,
            payload.uuid.toString(),
            payload,
        )
    }

    override fun count(
        statuses: List<IngestionHookPayloadStatus>?,
        outcome: IngestionEventProcessingResult?,
        gitHubDelivery: String?,
        gitHubEvent: String?,
        repository: String?,
        owner: String?,
        routing: String?,
        queue: String?,
    ): Int {
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        return storageService.count(
            store = INGESTION_HOOK_PAYLOAD_STORE,
            query = query(statuses, outcome, gitHubDelivery, gitHubEvent, repository, owner, routing, queue),
            queryVariables = queryVariables(statuses, outcome, gitHubDelivery, gitHubEvent, repository, owner, routing, queue),
        )
    }

    override fun list(
        offset: Int,
        size: Int,
        statuses: List<IngestionHookPayloadStatus>?,
        outcome: IngestionEventProcessingResult?,
        gitHubDelivery: String?,
        gitHubEvent: String?,
        repository: String?,
        owner: String?,
        routing: String?,
        queue: String?,
    ): List<IngestionHookPayload> {
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        val query: String? = query(statuses, outcome, gitHubDelivery, gitHubEvent, repository, owner, routing, queue)
        return storageService.filter(
            store = INGESTION_HOOK_PAYLOAD_STORE,
            type = IngestionHookPayload::class,
            offset = offset,
            size = size,
            orderQuery = "order by data->>'timestamp' desc",
            query = query,
            queryVariables = queryVariables(statuses, outcome, gitHubDelivery, gitHubEvent, repository, owner, routing, queue),
        )
    }

    override fun findByUUID(uuid: String): IngestionHookPayload? {
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        return storageService.find(
            INGESTION_HOOK_PAYLOAD_STORE,
            uuid,
            IngestionHookPayload::class
        )
    }

    private fun query(
        statuses: List<IngestionHookPayloadStatus>?,
        outcome: IngestionEventProcessingResult?,
        gitHubDelivery: String?,
        gitHubEvent: String?,
        repository: String?,
        owner: String?,
        routing: String?,
        queue: String?,
    ): String? {
        val parts = mutableListOf<String>()
        if (statuses != null && statuses.isNotEmpty()) {
            parts += "(" + statuses.joinToString(" OR ") { status ->
                "data->>'status' = '$status'"
            } + ")"
        }
        if (outcome != null) {
            parts += "data->>'outcome' = :outcome"
        }
        if (!gitHubDelivery.isNullOrBlank()) {
            parts += "data->>'gitHubDelivery' = :gitHubDelivery"
        }
        if (!gitHubEvent.isNullOrBlank()) {
            parts += "data->>'gitHubEvent' = :gitHubEvent"
        }
        if (!repository.isNullOrBlank()) {
            parts += "data->'repository'->>'name' = :repository"
        }
        if (!owner.isNullOrBlank()) {
            parts += "data->'repository'->'owner'->>'login' = :owner"
        }
        if (!routing.isNullOrBlank()) {
            parts += "data->>'routing' = :routing"
        }
        if (!queue.isNullOrBlank()) {
            parts += "data->>'queue' = :queue"
        }
        return if (parts.isNotEmpty()) {
            parts.joinToString(" AND ") { "( $it )" }
        } else {
            null
        }
    }

    private fun queryVariables(
        @Suppress("UNUSED_PARAMETER") statuses: List<IngestionHookPayloadStatus>?,
        outcome: IngestionEventProcessingResult?,
        gitHubDelivery: String?,
        gitHubEvent: String?,
        repository: String?,
        owner: String?,
        routing: String?,
        queue: String?,
    ): Map<String, *>? {
        val variables = mutableMapOf<String, Any?>()
        if (outcome != null) {
            variables["outcome"] = outcome.name
        }
        if (!gitHubDelivery.isNullOrBlank()) {
            variables["gitHubDelivery"] = gitHubDelivery
        }
        if (!gitHubEvent.isNullOrBlank()) {
            variables["gitHubEvent"] = gitHubEvent
        }
        if (!repository.isNullOrBlank()) {
            variables["repository"] = repository
        }
        if (!owner.isNullOrBlank()) {
            variables["owner"] = owner
        }
        if (!routing.isNullOrBlank()) {
            variables["routing"] = routing
        }
        if (!queue.isNullOrBlank()) {
            variables["queue"] = queue
        }
        return variables.takeIf { it.isNotEmpty() }
    }

    override fun cleanUntil(until: LocalDateTime): Int {
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        return storageService.deleteWithFilter(
            store = INGESTION_HOOK_PAYLOAD_STORE,
            query = "data->>'timestamp' < :until",
            queryVariables = mapOf(
                "until" to until.format(DateTimeFormatter.ISO_DATE_TIME),
            ),
        )
    }

    companion object {
        private const val INGESTION_HOOK_PAYLOAD_STORE = "github.IngestionHookPayload"
    }

}