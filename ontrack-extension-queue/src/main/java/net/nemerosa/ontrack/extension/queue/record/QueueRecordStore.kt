package net.nemerosa.ontrack.extension.queue.record

import net.nemerosa.ontrack.extension.queue.QueuePayload
import net.nemerosa.ontrack.model.pagination.PaginatedList
import java.time.LocalDateTime

@Deprecated("Use the recordings service")
interface QueueRecordStore {
    fun start(queuePayload: QueuePayload)

    fun save(queuePayload: QueuePayload, code: (QueueRecord) -> QueueRecord)

    fun save(record: QueueRecord)

    fun findByQueuePayloadID(id: String): QueueRecord?

    fun findByFilter(filter: QueueRecordQueryFilter, offset: Int, size: Int): PaginatedList<QueueRecord>

    fun removeAllBefore(retentionDate: LocalDateTime, nonRunningOnly: Boolean): Int
    fun removeAll()
}