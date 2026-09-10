package net.nemerosa.ontrack.extension.notifications.recording

import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.structure.ProjectEntityID

/**
 * Recording the notifications
 */
interface NotificationRecordingService {

    fun clearAll()

    fun record(record: NotificationRecord): String

    fun filter(filter: NotificationRecordFilter): PaginatedList<NotificationRecord>

    /**
     * The most recent records of one [channel] for each of the given [entities], most recent first.
     *
     * The batched form of [filter] with an `eventEntityId`, for a caller which has a whole page of
     * entities to resolve at once - the delivery map of a branch reads the workflows of every
     * promotion run on it (#1711), and one query per checkpoint would put a scan of STORAGE on the
     * page for each.
     *
     * [maxPerEntity] caps the records read **per entity**, never across all of them: a global cap
     * would silently starve whichever entities sorted last, and an entity with nothing to show is
     * not the same as an entity whose records were crowded out by another's. That is what makes
     * this a windowed query rather than one `IN` clause with a limit.
     *
     * @param channel Channel of the records to look for
     * @param entities Entities targeted by the event which triggered the notification
     * @param maxPerEntity Maximum number of records read for each entity
     * @return Records by entity. An entity with no record at all is absent from the map.
     */
    fun findByEntities(
        channel: String,
        entities: Collection<ProjectEntityID>,
        maxPerEntity: Int,
    ): Map<ProjectEntityID, List<NotificationRecord>>

    /**
     * Gets a record using its ID
     */
    fun findRecordById(id: String): NotificationRecord?

    fun clear(retentionSeconds: Long)

}