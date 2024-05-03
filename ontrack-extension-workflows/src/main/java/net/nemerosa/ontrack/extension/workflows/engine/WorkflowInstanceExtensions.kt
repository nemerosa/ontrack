package net.nemerosa.ontrack.extension.workflows.engine

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowNode
import java.util.*

fun createInstance(workflow: Workflow, context: JsonNode) = WorkflowInstance(
    id = UUID.randomUUID().toString(),
    workflow = workflow,
    context = context,
    nodesExecutions = workflow.nodes.map { it.toStartExecution() },
)

private fun WorkflowNode.toStartExecution() = WorkflowInstanceNode(
    id = id,
    status = WorkflowInstanceNodeStatus.IDLE,
    output = null,
)
