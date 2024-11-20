package net.nemerosa.ontrack.extension.workflows.engine

import net.nemerosa.ontrack.model.events.SerializableEvent
import net.nemerosa.ontrack.model.pagination.PaginatedList

interface WorkflowInstanceStore {

    fun create(instance: WorkflowInstance): WorkflowInstance

    fun saveNode(
        instance: WorkflowInstance,
        nodeId: String,
        nodeUpdate: (node: WorkflowInstanceNode) -> WorkflowInstanceNode
    ): WorkflowInstance
    
    fun saveEvent(instance: WorkflowInstance, event: SerializableEvent): WorkflowInstance

    fun error(instance: WorkflowInstance, message: String, throwable: Exception): WorkflowInstance

    fun stop(instance: WorkflowInstance)

    fun findById(id: String): WorkflowInstance?

    fun findByFilter(
        workflowInstanceFilter: WorkflowInstanceFilter = WorkflowInstanceFilter(),
    ): PaginatedList<WorkflowInstance>

    fun clearAll()

    fun cleanup()

}