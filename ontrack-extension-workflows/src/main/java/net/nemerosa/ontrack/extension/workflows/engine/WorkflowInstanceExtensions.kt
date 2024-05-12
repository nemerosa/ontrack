package net.nemerosa.ontrack.extension.workflows.engine

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowNode
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

fun createInstanceId(
    timestamp: LocalDateTime = Time.now(),
): String {
    val timestampText = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(timestamp)
    val uuid = UUID.randomUUID().toString()
    return "${timestampText}-${uuid}"
}

fun createInstance(
    workflow: Workflow,
    context: WorkflowContext,
    timestamp: LocalDateTime = Time.now(),
) = WorkflowInstance(
    id = createInstanceId(timestamp),
    timestamp = timestamp,
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
