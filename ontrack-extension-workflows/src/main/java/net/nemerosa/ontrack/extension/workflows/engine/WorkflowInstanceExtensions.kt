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
    contextContribution: (context: WorkflowContext, instanceId: String) -> WorkflowContext = { ctx, _ -> ctx },
): WorkflowInstance {
    val instanceId = createInstanceId(timestamp)
    return WorkflowInstance(
        id = instanceId,
        timestamp = timestamp,
        workflow = workflow,
        context = contextContribution(context, instanceId),
        nodesExecutions = workflow.nodes.map { it.toStartExecution() },
    )
}

private fun WorkflowNode.toStartExecution() = WorkflowInstanceNode(
    id = id,
    status = WorkflowInstanceNodeStatus.IDLE,
    output = null,
    error = null,
)
