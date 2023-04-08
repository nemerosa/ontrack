package net.nemerosa.ontrack.extension.queue.record

import net.nemerosa.ontrack.model.pagination.PaginatedList

@Deprecated("Using the recordings query service")
interface QueueRecordQueryService {

    fun findByQueuePayloadID(id: String): QueueRecord?

    fun findByFilter(filter: QueueRecordQueryFilter, offset: Int, size: Int): PaginatedList<QueueRecord>

}