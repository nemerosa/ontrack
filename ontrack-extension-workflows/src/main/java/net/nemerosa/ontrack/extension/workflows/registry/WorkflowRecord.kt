package net.nemerosa.ontrack.extension.workflows.registry

import net.nemerosa.ontrack.extension.workflows.definition.Workflow

data class WorkflowRecord(
    val id: String,
    val workflow: Workflow,
)
