package net.nemerosa.ontrack.extension.notifications.recording

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.json.format
import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntityID
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

@Service
@Transactional
class DefaultNotificationRecordingService(
    private val storageService: StorageService,
    private val securityService: SecurityService,
) : NotificationRecordingService {

    override fun clearAll() {
        securityService.checkGlobalFunction(NotificationRecordingAccess::class.java)
        storageService.deleteWithFilter(STORE)
    }

    override fun clear(retentionSeconds: Long) {
        securityService.checkGlobalFunction(NotificationRecordingAccess::class.java)
        val ref = Time.now() - Duration.ofSeconds(retentionSeconds)
        storageService.deleteWithFilter(
            store = STORE,
            query = "data::jsonb->>'timestamp' <= :timestamp",
            queryVariables = mapOf(
                "timestamp" to Time.store(ref)
            )
        )
    }

    override fun findRecordById(id: String): NotificationRecord? {
        securityService.checkGlobalFunction(NotificationRecordingAccess::class.java)

        return storageService.find(
            STORE,
            id,
            NotificationRecord::class
        )
    }

    override fun filter(filter: NotificationRecordFilter): PaginatedList<NotificationRecord> {
        securityService.checkGlobalFunction(NotificationRecordingAccess::class.java)

        val queries = mutableListOf<String>()
        val queryVariables = mutableMapOf<String, Any>()

        if (!filter.channel.isNullOrBlank()) {
            queries += "data::jsonb->>'channel' = :channel"
            queryVariables["channel"] = filter.channel
        }

        if (filter.resultType != null) {
            queries += "data::jsonb->'result'->>'type' = :resultType"
            queryVariables["resultType"] = filter.resultType.name
        }

        if (!filter.sourceId.isNullOrBlank()) {
            queries += "data::jsonb->'source'->>'id' = :sourceId"
            queryVariables["sourceId"] = filter.sourceId
            if (filter.sourceData != null) {
                queries += "data::jsonb->'source'->'data' @> CAST(:sourceData AS JSONB)"
                queryVariables["sourceData"] = filter.sourceData.format()
            }
        }

        if (filter.eventEntityId != null) {
            // This predicate is served by the partial expression indexes of
            // V80__1653_notification_record_entity_index.sql. Rewording it silently makes those
            // indexes unusable and turns this lookup back into a full scan of STORAGE --
            // NotificationRecordEntityIndexIT guards the match.
            queries += "(data::jsonb->'event'->'entities'->'${filter.eventEntityId.type.name}'->>'id')::int = :eventEntityId"
            queryVariables["eventEntityId"] = filter.eventEntityId.id
        }

        val query = queries.joinToString(" AND ") { "( $it )" }

        val total = storageService.count(
            store = STORE,
            query = query,
            queryVariables = queryVariables,
        )

        val records = storageService.filter(
            store = STORE,
            type = NotificationRecord::class,
            query = query,
            queryVariables = queryVariables,
            offset = filter.offset,
            size = filter.size,
            orderQuery = "ORDER BY data::jsonb->>'timestamp' DESC"
        )

        return PaginatedList.create(records, filter.offset, filter.size, total)
    }

    override fun findByEntities(
        channel: String,
        entities: Collection<ProjectEntityID>,
        maxPerEntity: Int,
    ): Map<ProjectEntityID, List<NotificationRecord>> {
        securityService.checkGlobalFunction(NotificationRecordingAccess::class.java)
        if (entities.isEmpty() || maxPerEntity <= 0) return emptyMap()

        // One query per entity TYPE, because the entity id lives at a path named after the type and
        // the partial indexes of V80 are one per type too. Every caller so far passes entities of a
        // single type, so this is one query in practice; a mixed call degrades to a handful rather
        // than to one per entity, which is the cost this method exists to avoid.
        return entities.groupBy { it.type }
            .flatMap { (type, ids) -> findByEntitiesOfType(channel, type, ids, maxPerEntity).toList() }
            .toMap()
    }

    private fun findByEntitiesOfType(
        channel: String,
        type: ProjectEntityType,
        entities: List<ProjectEntityID>,
        maxPerEntity: Int,
    ): Map<ProjectEntityID, List<NotificationRecord>> {
        val entityIds = entities.map { it.id }.distinct()

        // A window over the records of each entity, so that the cap is per entity. The inner scan is
        // the same predicate `filter` uses for one entity - written character for character, because
        // the partial expression indexes of V80__1653_notification_record_entity_index.sql are the
        // only thing keeping this off a full scan of STORAGE, and rewording it makes them unusable.
        val entityPath = "DATA->'event'->'entities'->'${type.name}'->>'id'"
        val query = """
            NAME IN (
                SELECT windowed.NAME FROM (
                    SELECT records.NAME AS NAME,
                           ROW_NUMBER() OVER (
                               PARTITION BY (records.$entityPath)::int
                               ORDER BY records.DATA->>'timestamp' DESC
                           ) AS position
                    FROM STORAGE records
                    WHERE records.STORE = :store
                      AND records.DATA->>'channel' = :channel
                      AND (records.$entityPath)::int IN (:entityIds)
                ) windowed
                WHERE windowed.position <= :maxPerEntity
            )
        """.trimIndent()

        val records = storageService.filter(
            store = STORE,
            type = NotificationRecord::class,
            offset = 0,
            // The window has already capped the result; this only has to be big enough not to cut it
            size = entityIds.size * maxPerEntity,
            query = query,
            queryVariables = mapOf(
                "channel" to channel,
                "entityIds" to entityIds,
                "maxPerEntity" to maxPerEntity,
            ),
            orderQuery = "ORDER BY DATA->>'timestamp' DESC",
        )

        // Which entity a record belongs to is read back from the record itself rather than returned
        // by the query: the record carries its own event, so nothing has to be joined back.
        val byId = entities.associateBy { it.id }
        return records
            .groupBy { record ->
                record.event.path("entities").path(type.name).path("id").asInt()
            }
            .mapNotNull { (id, entityRecords) -> byId[id]?.let { it to entityRecords } }
            .toMap()
    }

    override fun record(record: NotificationRecord): String {
        storageService.store(
            STORE,
            record.id,
            record,
        )
        return record.id
    }

    companion object {
        internal val STORE = NotificationRecord::class.java.name
    }

}