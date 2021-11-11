package net.nemerosa.ontrack.extension.github.ingestion.payload

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.StorageService
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Component
@Transactional
class DefaultIngestionHookPayloadStorage(
    private val storageService: StorageService,
    private val securityService: SecurityService,
) : IngestionHookPayloadStorage {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun store(payload: IngestionHookPayload) {
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        storageService.store(
            INGESTION_HOOK_PAYLOAD_STORE,
            payload.uuid.toString(),
            payload,
        )
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun start(payload: IngestionHookPayload) {
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        // Gets the old payload
        val old = getByUUID(payload.uuid)
        // Saves the new version
        storageService.store(
            INGESTION_HOOK_PAYLOAD_STORE,
            payload.uuid.toString(),
            IngestionHookPayload(
                uuid = payload.uuid,
                timestamp = old.timestamp,
                gitHubDelivery = old.gitHubDelivery,
                gitHubEvent = old.gitHubEvent,
                gitHubHookID = old.gitHubHookID,
                gitHubHookInstallationTargetID = old.gitHubHookInstallationTargetID,
                gitHubHookInstallationTargetType = old.gitHubHookInstallationTargetType,
                payload = old.payload,
                repository = old.repository,
                status = IngestionHookPayloadStatus.PROCESSING,
                started = Time.now(),
                message = null,
                completion = null,
            )
        )
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun finished(payload: IngestionHookPayload) {
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        // Gets the old payload
        val old = getByUUID(payload.uuid)
        // Saves the new version
        storageService.store(
            INGESTION_HOOK_PAYLOAD_STORE,
            payload.uuid.toString(),
            IngestionHookPayload(
                uuid = payload.uuid,
                timestamp = old.timestamp,
                gitHubDelivery = old.gitHubDelivery,
                gitHubEvent = old.gitHubEvent,
                gitHubHookID = old.gitHubHookID,
                gitHubHookInstallationTargetID = old.gitHubHookInstallationTargetID,
                gitHubHookInstallationTargetType = old.gitHubHookInstallationTargetType,
                payload = old.payload,
                repository = old.repository,
                status = IngestionHookPayloadStatus.COMPLETED,
                started = old.started,
                message = null,
                completion = Time.now(),
            )
        )
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun error(payload: IngestionHookPayload, any: Throwable) {
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        // Gets the old payload
        val old = getByUUID(payload.uuid)
        // Saves the new version
        storageService.store(
            INGESTION_HOOK_PAYLOAD_STORE,
            payload.uuid.toString(),
            IngestionHookPayload(
                uuid = payload.uuid,
                timestamp = old.timestamp,
                gitHubDelivery = old.gitHubDelivery,
                gitHubEvent = old.gitHubEvent,
                gitHubHookID = old.gitHubHookID,
                gitHubHookInstallationTargetID = old.gitHubHookInstallationTargetID,
                gitHubHookInstallationTargetType = old.gitHubHookInstallationTargetType,
                payload = old.payload,
                repository = old.repository,
                status = IngestionHookPayloadStatus.ERRORED,
                started = old.started,
                message = ExceptionUtils.getStackTrace(any),
                completion = Time.now(),
            )
        )
    }

    private fun getByUUID(uuid: UUID) = findByUUID(uuid.toString())
        ?: throw IngestionHookPayloadUUIDNotFoundException(uuid)

    override fun count(
        statuses: List<IngestionHookPayloadStatus>?,
        gitHubDelivery: String?,
        gitHubEvent: String?,
        repository: String?,
        owner: String?,
    ): Int {
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        return storageService.count(
            store = INGESTION_HOOK_PAYLOAD_STORE,
            query = query(statuses, gitHubDelivery, gitHubEvent, repository, owner),
            queryVariables = queryVariables(statuses, gitHubDelivery, gitHubEvent, repository, owner),
        )
    }

    override fun list(
        offset: Int,
        size: Int,
        statuses: List<IngestionHookPayloadStatus>?,
        gitHubDelivery: String?,
        gitHubEvent: String?,
        repository: String?,
        owner: String?
    ): List<IngestionHookPayload> {
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        val query: String? = query(statuses, gitHubDelivery, gitHubEvent, repository, owner)
        return storageService.filter(
            store = INGESTION_HOOK_PAYLOAD_STORE,
            type = IngestionHookPayload::class,
            offset = offset,
            size = size,
            orderQuery = "order by data->>'timestamp' desc",
            query = query,
            queryVariables = queryVariables(statuses, gitHubDelivery, gitHubEvent, repository, owner),
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
        gitHubDelivery: String?,
        gitHubEvent: String?,
        repository: String?,
        owner: String?,
    ): String? {
        val parts = mutableListOf<String>()
        if (statuses != null && statuses.isNotEmpty()) {
            parts += "(" + statuses.joinToString(" OR ") { status ->
                "data->>'status' = '$status'"
            } + ")"
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
        return if (parts.isNotEmpty()) {
            parts.joinToString(" AND ") { "( $it )" }
        } else {
            null
        }
    }

    private fun queryVariables(
        @Suppress("UNUSED_PARAMETER") statuses: List<IngestionHookPayloadStatus>?,
        gitHubDelivery: String?,
        gitHubEvent: String?,
        repository: String?,
        owner: String?,
    ): Map<String, *>? {
        val variables = mutableMapOf<String, Any?>()
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