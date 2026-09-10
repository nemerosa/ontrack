package net.nemerosa.ontrack.extension.workflows.notifications

import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance
import net.nemerosa.ontrack.model.structure.ProjectEntityID

/**
 * The workflows which have been launched by a notification on an event targeting a project entity.
 *
 * There is no direct link between an entity and a workflow instance: the link goes through the
 * notification records of the `workflow` channel, whose output carries the workflow instance ID.
 * That indirection is what makes the lookup worth a service of its own - two callers now read it,
 * the `workflowInstances` GraphQL field and the delivery map (#1711), and they must agree on how
 * far back it looks.
 *
 * Reading notification records requires the `NotificationRecordingAccess` global function, while a
 * workflow instance is readable by any authenticated user, so the record half runs as admin. Only
 * instance IDs escape that privileged block - a record itself is never returned here.
 */
interface EntityWorkflowInstanceService {

    /**
     * Workflow instances launched for one [entity], most recent first.
     */
    fun findWorkflowInstancesByEntity(entity: ProjectEntityID): List<WorkflowInstance>

    /**
     * Workflow instances launched for each of the given [entities], most recent first.
     *
     * The batched form, for a caller resolving a whole page of entities at once. It costs one record
     * query and one instance query whatever the number of entities, where calling
     * [findWorkflowInstancesByEntity] in a loop costs two each.
     *
     * [MAX_RECORDS] still applies **per entity**, exactly as it does for one: a cap spread across
     * all of them would silently starve whichever entities sorted last, and an entity with no
     * workflows would then be indistinguishable from one whose records were crowded out.
     *
     * @return Instances by entity. An entity with none is absent from the map.
     */
    fun findWorkflowInstancesByEntities(
        entities: Collection<ProjectEntityID>,
    ): Map<ProjectEntityID, List<WorkflowInstance>>

    companion object {
        /**
         * Maximum number of notification records scanned for one entity.
         *
         * The list may therefore be truncated on entities which accumulate many of them, like a
         * project or a branch.
         */
        const val MAX_RECORDS = 100
    }
}
