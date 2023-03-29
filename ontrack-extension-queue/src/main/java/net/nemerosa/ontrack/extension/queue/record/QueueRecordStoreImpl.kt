package net.nemerosa.ontrack.extension.queue.record

import net.nemerosa.ontrack.extension.queue.QueuePayload
import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Component

@Component
class QueueRecordStoreImpl(
    private val store: StorageService,
) : QueueRecordStore {

    override fun start(queuePayload: QueuePayload) {
        val record = QueueRecord.create(queuePayload)
        store.store(
            STORE,
            queuePayload.id,
            record
        )
    }

    override fun findByQueuePayloadID(id: String): QueueRecord? =
        store.find(STORE, id, QueueRecord::class)

    override fun save(queuePayload: QueuePayload, code: (QueueRecord) -> QueueRecord) {
        val oldRecord = getRecord(queuePayload.id)
        val newRecord = code(oldRecord)
        store.store(STORE, queuePayload.id, newRecord)
    }

    override fun findByFilter(filter: QueueRecordQueryFilter, offset: Int, size: Int): PaginatedList<QueueRecord> {

        val queries = mutableListOf<String>()
        val queryVariables = mutableMapOf<String, String>()

        if (filter.id != null) {
            queries += "data::jsonb->'queuePayload'->>'id' = :id"
            queryVariables["id"] = filter.id
        }

        val query = queries.joinToString(" AND ") { "( $it )" }

        return store.paginatedFilter(
            store = STORE,
            type = QueueRecord::class,
            offset = offset,
            size = size,
            query = query,
            queryVariables = queryVariables,
        )
    }

    private fun getRecord(id: String): QueueRecord =
        findByQueuePayloadID(id)
            ?: throw QueueRecordNotFoundException(id)

    companion object {
        private val STORE = QueueRecordStore::class.java.name
    }
}