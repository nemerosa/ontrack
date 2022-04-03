package net.nemerosa.ontrack.extension.notifications.recording

import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class DefaultNotificationRecordingService(
    private val storageService: StorageService,
) : NotificationRecordingService {

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