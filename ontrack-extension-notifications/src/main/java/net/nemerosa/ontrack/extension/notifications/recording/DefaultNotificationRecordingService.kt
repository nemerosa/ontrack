package net.nemerosa.ontrack.extension.notifications.recording

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.util.*

@Service
@Transactional
class DefaultNotificationRecordingService(
    private val storageService: StorageService,
    private val securityService: SecurityService,
) : NotificationRecordingService {

    override fun clearAll() {
        securityService.checkGlobalFunction(NotificationRecordingAccess::class.java)
        storageService.deleteWithFilter(STORE)
    }

    override fun clear(retentionSeconds: Long) {
        securityService.checkGlobalFunction(NotificationRecordingAccess::class.java)
        val ref = Time.now() - Duration.ofSeconds(retentionSeconds)
        storageService.deleteWithFilter(
            store = STORE,
            query = "data::jsonb->>'timestamp' <= :timestamp",
            queryVariables = mapOf(
                "timestamp" to Time.store(ref)
            )
        )
    }

    override fun filter(filter: NotificationRecordFilter): PaginatedList<NotificationRecord> {
        securityService.checkGlobalFunction(NotificationRecordingAccess::class.java)

        val queries = mutableListOf<String>()
        val queryVariables = mutableMapOf<String, String>()

        if (filter.resultType != null) {
            queries += "data::jsonb->'result'->>'type' = :resultType"
            queryVariables["resultType"] = filter.resultType.name
        }

        val query = queries.joinToString(" AND ") { "( $it )" }

        val total = storageService.count(
            store = STORE,
            query = query,
            queryVariables = queryVariables,
        )

        val records = storageService.filter(
            store = STORE,
            type = NotificationRecord::class,
            query = query,
            queryVariables = queryVariables,
            offset = filter.offset,
            size = filter.size,
            orderQuery = "ORDER BY data::jsonb->>'timestamp' DESC"
        )

        return PaginatedList.create(records, filter.offset, filter.size, total)
    }

    override fun record(record: NotificationRecord): String {
        val id = UUID.randomUUID().toString()
        storageService.store(
            STORE,
            id,
            record,
        )
        return id
    }

    companion object {
        private val STORE = NotificationRecord::class.java.name
    }

}