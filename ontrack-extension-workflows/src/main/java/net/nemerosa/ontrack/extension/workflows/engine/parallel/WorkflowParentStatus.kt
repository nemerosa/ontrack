package net.nemerosa.ontrack.extension.workflows.engine.parallel

import net.nemerosa.ontrack.extension.workflows.definition.WorkflowParentNode
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstanceNodeStatus

data class WorkflowParentStatus(
    val parentDef: WorkflowParentNode,
    val status: WorkflowInstanceNodeStatus?,
)
