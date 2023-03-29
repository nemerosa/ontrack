package net.nemerosa.ontrack.extension.queue.record

import net.nemerosa.ontrack.extension.queue.QueuePayload
import net.nemerosa.ontrack.model.pagination.PaginatedList

interface QueueRecordStore {
    fun start(queuePayload: QueuePayload)

    fun save(queuePayload: QueuePayload, code: (QueueRecord) -> QueueRecord)

    fun findByQueuePayloadID(id: String): QueueRecord?

    fun findByFilter(filter: QueueRecordQueryFilter, offset: Int, size: Int): PaginatedList<QueueRecord>
}