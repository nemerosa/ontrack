package net.nemerosa.ontrack.extension.queue.record

import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.security.ApplicationManagement
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class QueueRecordQueryServiceImpl(
        private val queueRecordingsExtension: QueueRecordingsExtension,
        private val securityService: SecurityService,
        private val queueRecordStore: QueueRecordStore,
) : QueueRecordQueryService {

    override fun findByQueuePayloadID(id: String): QueueRecord? {
        securityService.checkGlobalFunction(ApplicationManagement::class.java)
        return queueRecordStore.findByQueuePayloadID(id)
    }

    override fun findByFilter(filter: QueueRecordQueryFilter, offset: Int, size: Int): PaginatedList<QueueRecord> {
        securityService.checkGlobalFunction(ApplicationManagement::class.java)
        return queueRecordStore.findByFilter(filter, offset, size)
    }

}