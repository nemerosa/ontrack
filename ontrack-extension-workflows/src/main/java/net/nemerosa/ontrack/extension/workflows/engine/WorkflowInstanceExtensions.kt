package net.nemerosa.ontrack.extension.workflows.engine

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowNode
import java.time.format.DateTimeFormatter
import java.util.*

fun createInstanceId(): String {
    val timestamp = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(Time.now)
    val uuid = UUID.randomUUID().toString()
    return "${timestamp}-${uuid}"
}

fun createInstance(workflow: Workflow, context: WorkflowContext) = WorkflowInstance(
    id = createInstanceId(),
    workflow = workflow,
    context = context,
    nodesExecutions = workflow.nodes.map { it.toStartExecution() },
)

private fun WorkflowNode.toStartExecution() = WorkflowInstanceNode(
    id = id,
    status = WorkflowInstanceNodeStatus.IDLE,
    output = null,
    error = null,
)
