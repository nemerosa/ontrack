package net.nemerosa.ontrack.extension.queue.record

import net.nemerosa.ontrack.extension.queue.QueuePayload
import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.support.StorageService
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class QueueRecordStoreImpl(
        private val store: StorageService,
) : QueueRecordStore {

    override fun start(queuePayload: QueuePayload) {
        val record = QueueRecord.create(queuePayload)
        save(record)
    }

    override fun findByQueuePayloadID(id: String): QueueRecord? =
            store.find(STORE, id, QueueRecord::class)

    override fun save(queuePayload: QueuePayload, code: (QueueRecord) -> QueueRecord) {
        val oldRecord = getRecord(queuePayload.id)
        val newRecord = code(oldRecord)
        save(newRecord)
    }

    override fun save(record: QueueRecord) {
        store.store(STORE, record.queuePayload.id, record)
    }

    override fun findByFilter(filter: QueueRecordQueryFilter, offset: Int, size: Int): PaginatedList<QueueRecord> {

        val queries = mutableListOf<String>()
        val queryVariables = mutableMapOf<String, String>()

        if (!filter.id.isNullOrBlank()) {
            queries += "data::jsonb->'queuePayload'->>'id' = :id"
            queryVariables["id"] = filter.id
        }

        if (!filter.processor.isNullOrBlank()) {
            queries += "data::jsonb->'queuePayload'->>'processor' = :processor"
            queryVariables["processor"] = filter.processor
        }

        if (filter.state != null) {
            queries += "data::jsonb->>'state' = :state"
            queryVariables["state"] = filter.state.name
        }

        if (!filter.routingKey.isNullOrBlank()) {
            queries += "data::jsonb->>'routingKey' = :routingKey"
            queryVariables["routingKey"] = filter.routingKey
        }

        if (!filter.queueName.isNullOrBlank()) {
            queries += "data::jsonb->>'queueName' = :queueName"
            queryVariables["queueName"] = filter.queueName
        }

        if (!filter.text.isNullOrBlank()) {
            queries += "(data::jsonb->'queuePayload'->>'body' LIKE :text) OR (data::jsonb->>'actualPayload' LIKE :text)"
            queryVariables["text"] = "%${filter.text}%"
        }

        val query = queries.joinToString(" AND ") { "( $it )" }

        return store.paginatedFilter(
                store = STORE,
                type = QueueRecord::class,
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

    private fun getRecord(id: String): QueueRecord =
            findByQueuePayloadID(id)
                    ?: throw QueueRecordNotFoundException(id)

    companion object {
        private val STORE = QueueRecordStore::class.java.name
    }
}