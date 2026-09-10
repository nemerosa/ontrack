package net.nemerosa.ontrack.extension.workflows.notifications

import net.nemerosa.ontrack.extension.notifications.recording.NotificationRecord
import net.nemerosa.ontrack.extension.notifications.recording.NotificationRecordFilter
import net.nemerosa.ontrack.extension.notifications.recording.NotificationRecordingService
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowEngine
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance
import net.nemerosa.ontrack.json.parseOrNull
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntityID
import org.springframework.stereotype.Service

@Service
class EntityWorkflowInstanceServiceImpl(
    private val notificationRecordingService: NotificationRecordingService,
    private val workflowEngine: WorkflowEngine,
    private val securityService: SecurityService,
) : EntityWorkflowInstanceService {

    override fun findWorkflowInstancesByEntity(entity: ProjectEntityID): List<WorkflowInstance> {
        val instanceIds = securityService.asAdmin {
            notificationRecordingService.filter(
                NotificationRecordFilter(
                    offset = 0,
                    size = EntityWorkflowInstanceService.MAX_RECORDS,
                    channel = WorkflowNotificationChannel.TYPE,
                    eventEntityId = entity,
                )
            ).pageItems.instanceIds()
        }
        // Loaded in one batch: resolving them one by one would cost three queries and a transaction
        // each. Records outlive instances, so ids which no longer resolve are skipped, and the
        // record order (most recent first) is preserved.
        return workflowEngine.findWorkflowInstances(instanceIds)
    }

    override fun findWorkflowInstancesByEntities(
        entities: Collection<ProjectEntityID>,
    ): Map<ProjectEntityID, List<WorkflowInstance>> {
        if (entities.isEmpty()) return emptyMap()

        val idsByEntity = securityService.asAdmin {
            notificationRecordingService.findByEntities(
                channel = WorkflowNotificationChannel.TYPE,
                entities = entities,
                maxPerEntity = EntityWorkflowInstanceService.MAX_RECORDS,
            )
        }.mapValues { (_, records) -> records.instanceIds() }

        // ONE call for every entity at once, which is the whole point of this method: the engine
        // already batches, and asking it once per entity would put its query back on a loop.
        val instances = workflowEngine
            .findWorkflowInstances(idsByEntity.values.flatten().distinct())
            .associateBy { it.id }

        return idsByEntity
            .mapValues { (_, ids) -> ids.mapNotNull(instances::get) }
            .filterValues { it.isNotEmpty() }
    }

    /**
     * The workflow instance ids a run of records points at, most recent first and without repeats.
     *
     * Records outlive schema changes, so a record whose output cannot be parsed is skipped rather
     * than failing the lookup.
     */
    private fun List<NotificationRecord>.instanceIds(): List<String> =
        mapNotNull { record ->
            record.result.output?.parseOrNull<WorkflowNotificationChannelOutput>()?.workflowInstanceId
        }.distinct()

}
