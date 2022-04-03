package net.nemerosa.ontrack.extension.notifications.recording

import net.nemerosa.ontrack.model.pagination.PaginatedList

/**
 * Recording the notifications
 */
interface NotificationRecordingService {

    fun record(record: NotificationRecord)

    fun filter(filter: NotificationRecordFilter): PaginatedList<NotificationRecord>

}