package net.nemerosa.ontrack.extension.notifications.recording

import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultNotificationRecordingService(
    private val storageService: StorageService,
) : NotificationRecordingService {
}