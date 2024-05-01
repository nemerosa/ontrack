package net.nemerosa.ontrack.extension.workflows.registry

import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutor

data class WorkflowRecord(
    val id: String,
    val workflow: Workflow,
    val nodeExecutor: WorkflowNodeExecutor,
)
