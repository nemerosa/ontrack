package net.nemerosa.ontrack.extension.hook.records

import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.support.StorageService
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class HookRecordStoreImpl(
        private val store: StorageService,
) : HookRecordStore {

    override fun save(record: HookRecord) {
        store.store(
                STORE,
                record.id,
                record
        )
    }

    override fun deleteByFilter(filter: HookRecordQueryFilter) {
        val (query, queryVariables) = getQueryForFilter(filter)
        store.deleteWithFilter(
                STORE,
                query,
                queryVariables,
        )
    }

    override fun save(recordId: String, code: (HookRecord) -> HookRecord) {
        val oldRecord = getRecord(recordId)
        val newRecord = code(oldRecord)
        save(newRecord)
    }

    override fun findById(id: String): HookRecord? =
            store.find(STORE, id, HookRecord::class)

    override fun findByFilter(filter: HookRecordQueryFilter, offset: Int, size: Int): PaginatedList<HookRecord> {

        val (query, queryVariables) = getQueryForFilter(filter)

        return store.paginatedFilter(
                store = STORE,
                type = HookRecord::class,
                offset = offset,
                size = size,
                query = query,
                queryVariables = queryVariables,
                orderQuery = "ORDER BY data::jsonb->>'startTime' DESC",
        )
    }

    override fun removeAllBefore(retentionDate: LocalDateTime, nonRunningOnly: Boolean): Int =
            if (nonRunningOnly) {
                store.deleteWithFilter(
                        store = STORE,
                        query = "data::jsonb->>'endTime' IS NOT NULL AND data::jsonb->>'startTime' <= :beforeTime",
                        queryVariables = mapOf(
                                "beforeTime" to AbstractJdbcRepository.dateTimeForDB(retentionDate)
                        )
                )
            } else {
                store.deleteWithFilter(
                        store = STORE,
                        query = "data::jsonb->>'startTime' <= :beforeTime",
                        queryVariables = mapOf(
                                "beforeTime" to AbstractJdbcRepository.dateTimeForDB(retentionDate)
                        )
                )
            }

    override fun removeAll() {
        store.deleteWithFilter(STORE)
    }

    private fun getQueryForFilter(filter: HookRecordQueryFilter): Pair<String, Map<String, *>> {
        val queries = mutableListOf<String>()
        val queryVariables = mutableMapOf<String, String>()

        if (!filter.id.isNullOrBlank()) {
            queries += "data::jsonb->>'id' = :id"
            queryVariables["id"] = filter.id
        }

        if (!filter.hook.isNullOrBlank()) {
            queries += "data::jsonb->>'hook' = :hook"
            queryVariables["hook"] = filter.hook
        }

        if (filter.state != null) {
            queries += "data::jsonb->>'state' = :state"
            queryVariables["state"] = filter.state.name
        }

        if (!filter.text.isNullOrBlank()) {
            queries += "data::jsonb->'request'->>'body' LIKE :text"
            queryVariables["text"] = "%${filter.text}%"
        }

        val query = queries.joinToString(" AND ") { "( $it )" }

        return query to queryVariables
    }

    private fun getRecord(recordId: String): HookRecord =
            findById(recordId)
                    ?: throw HookRecordNotFoundException(recordId)

    companion object {
        private val STORE = HookRecordStore::class.java.name
    }
}