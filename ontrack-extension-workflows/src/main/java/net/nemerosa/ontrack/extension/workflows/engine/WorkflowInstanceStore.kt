package net.nemerosa.ontrack.extension.workflows.engine

import net.nemerosa.ontrack.model.pagination.PaginatedList

interface WorkflowInstanceStore {

    fun store(instance: WorkflowInstance): WorkflowInstance

    fun findById(id: String): WorkflowInstance?

    fun findByFilter(
        workflowInstanceFilter: WorkflowInstanceFilter = WorkflowInstanceFilter(),
    ): PaginatedList<WorkflowInstance>

    fun clearAll()

    fun cleanup()

}