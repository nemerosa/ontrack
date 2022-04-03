package net.nemerosa.ontrack.extension.notifications.recording

import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class DefaultNotificationRecordingService(
    private val storageService: StorageService,
    private val securityService: SecurityService,
) : NotificationRecordingService {

    override fun filter(filter: NotificationRecordFilter): PaginatedList<NotificationRecord> {
        securityService.checkGlobalFunction(NotificationRecordingAccess::class.java)

        val total = storageService.count(
            store = STORE,
        )

        val records = storageService.filter(
            store = STORE,
            type = NotificationRecord::class,
            offset = filter.offset,
            size = filter.size,
        )

        return PaginatedList.create(records, filter.offset, filter.size, total)
    }

    override fun record(record: NotificationRecord) {
        val id = UUID.randomUUID().toString()
        storageService.store(
            STORE,
            id,
            record,
        )
    }

    companion object {
        private val STORE = NotificationRecord::class.java.name
    }

}