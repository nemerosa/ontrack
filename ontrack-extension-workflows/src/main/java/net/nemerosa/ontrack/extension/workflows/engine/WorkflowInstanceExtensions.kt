package net.nemerosa.ontrack.extension.workflows.engine

import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowNode
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutor
import java.util.*

fun createInstance(workflow: Workflow, workflowNodeExecutor: WorkflowNodeExecutor) = WorkflowInstance(
    id = UUID.randomUUID().toString(),
    workflow = workflow,
    executorId = workflowNodeExecutor.id,
    nodesExecutions = workflow.nodes.map { it.toStartExecution() },
)

private fun WorkflowNode.toStartExecution() = WorkflowInstanceNode(
    id = id,
    status = WorkflowInstanceNodeStatus.IDLE,
    output = null,
)
