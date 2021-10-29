package net.nemerosa.ontrack.extension.github.ingestion.payload

import net.nemerosa.ontrack.common.Time
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
) : IngestionHookPayloadStorage {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun store(payload: IngestionHookPayload) {
        storageService.store(
            INGESTION_HOOK_PAYLOAD_STORE,
            payload.uuid.toString(),
            payload,
        )
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun start(payload: IngestionHookPayload) {
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
                status = IngestionHookPayloadStatus.PROCESSING,
                started = Time.now(),
                message = null,
                completion = null,
            )
        )
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun finished(payload: IngestionHookPayload) {
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
                status = IngestionHookPayloadStatus.COMPLETED,
                started = old.started,
                message = null,
                completion = Time.now(),
            )
        )
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun error(payload: IngestionHookPayload, any: Throwable) {
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
                status = IngestionHookPayloadStatus.ERRORED,
                started = old.started,
                message = ExceptionUtils.getStackTrace(any),
                completion = Time.now(),
            )
        )
    }

    private fun findByUUID(uuid: UUID) =
        storageService.find(INGESTION_HOOK_PAYLOAD_STORE, uuid.toString(), IngestionHookPayload::class)

    private fun getByUUID(uuid: UUID) = findByUUID(uuid)
        ?: throw IngestionHookPayloadUUIDNotFoundException(uuid)

    override fun count(): Int {
        return storageService.count(store = INGESTION_HOOK_PAYLOAD_STORE)
    }

    override fun list(
        offset: Int,
        size: Int,
        statuses: List<IngestionHookPayloadStatus>?,
    ): List<IngestionHookPayload> {
        val query: String? = if (statuses != null && statuses.isNotEmpty()) {
            "(" + statuses.joinToString(" OR ") { status ->
                "data->>'status' = '$status'"
            } + ")"
        } else {
            null
        }
        return storageService.filter(
            store = INGESTION_HOOK_PAYLOAD_STORE,
            type = IngestionHookPayload::class,
            offset = offset,
            size = size,
            orderQuery = "order by data->>'timestamp' desc",
            query = query,
            queryVariables = null,
        )
    }

    override fun cleanUntil(until: LocalDateTime) =
        storageService.deleteWithFilter(
            store = INGESTION_HOOK_PAYLOAD_STORE,
            query = "data->>'timestamp' < :until",
            queryVariables = mapOf(
                "until" to until.format(DateTimeFormatter.ISO_DATE_TIME),
            ),
        )

    companion object {
        private const val INGESTION_HOOK_PAYLOAD_STORE = "github.IngestionHookPayload"
    }

}