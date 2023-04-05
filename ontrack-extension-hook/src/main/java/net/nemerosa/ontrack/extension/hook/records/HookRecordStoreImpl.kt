package net.nemerosa.ontrack.extension.hook.records

import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Component

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

    override fun save(recordId: String, code: (HookRecord) -> HookRecord) {
        val oldRecord = getRecord(recordId)
        val newRecord = code(oldRecord)
        save(newRecord)
    }

    override fun findById(id: String): HookRecord? =
            store.find(STORE, id, HookRecord::class)

    override fun findByFilter(filter: HookRecordQueryFilter, offset: Int, size: Int): PaginatedList<HookRecord> {

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

    private fun getRecord(recordId: String): HookRecord =
            findById(recordId)
                    ?: throw HookRecordNotFoundException(recordId)

    companion object {
        private val STORE = HookRecordStore::class.java.name
    }
}