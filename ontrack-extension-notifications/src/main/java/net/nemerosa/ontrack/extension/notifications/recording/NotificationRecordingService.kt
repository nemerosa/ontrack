package net.nemerosa.ontrack.extension.notifications.recording

import net.nemerosa.ontrack.model.pagination.PaginatedList

/**
 * Recording the notifications
 */
interface NotificationRecordingService {

    fun clearAll()

    fun record(record: NotificationRecord): String

    fun filter(filter: NotificationRecordFilter): PaginatedList<NotificationRecord>

    /**
     * Gets a record using its ID
     */
    fun findRecordById(id: String): NotificationRecord?

    fun clear(retentionSeconds: Long)

}