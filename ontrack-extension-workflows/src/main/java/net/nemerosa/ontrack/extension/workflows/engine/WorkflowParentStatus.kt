package net.nemerosa.ontrack.extension.workflows.engine

import net.nemerosa.ontrack.extension.workflows.definition.WorkflowParentNode

data class WorkflowParentStatus(
    val parentDef: WorkflowParentNode,
    val status: WorkflowInstanceNodeStatus?,
)
