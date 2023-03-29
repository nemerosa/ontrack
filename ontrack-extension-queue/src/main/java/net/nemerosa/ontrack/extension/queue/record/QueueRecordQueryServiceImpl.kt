package net.nemerosa.ontrack.extension.queue.record

import net.nemerosa.ontrack.model.pagination.PaginatedList
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class QueueRecordQueryServiceImpl(
    private val queueRecordStore: QueueRecordStore,
) : QueueRecordQueryService {

    override fun findByQueuePayloadID(id: String): QueueRecord? =
        queueRecordStore.findByQueuePayloadID(id)

    override fun findByFilter(filter: QueueRecordQueryFilter, offset: Int, size: Int): PaginatedList<QueueRecord> =
        queueRecordStore.findByFilter(filter, offset, size)

}