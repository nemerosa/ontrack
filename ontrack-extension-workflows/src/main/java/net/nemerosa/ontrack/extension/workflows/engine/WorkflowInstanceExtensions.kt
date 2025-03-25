package net.nemerosa.ontrack.extension.workflows.engine

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowNode
import net.nemerosa.ontrack.model.events.SerializableEvent
import net.nemerosa.ontrack.model.templating.TemplatingContextData
import net.nemerosa.ontrack.model.trigger.TriggerData
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
    event: SerializableEvent,
    triggerData: TriggerData,
    contexts: Map<String, TemplatingContextData>,
    timestamp: LocalDateTime = Time.now(),
): WorkflowInstance {
    val instanceId = createInstanceId(timestamp)
    val eventWithInstance = event.withValue(
        WorkflowInstance.EVENT_INSTANCE_ID,
        instanceId
    )
    return WorkflowInstance(
        id = instanceId,
        timestamp = timestamp,
        workflow = workflow,
        event = eventWithInstance,
        triggerData = triggerData,
        contexts = contexts,
        nodesExecutions = workflow.nodes.map { it.toStartExecution() },
    )
}

private fun WorkflowNode.toStartExecution() = WorkflowInstanceNode(
    id = id,
    status = WorkflowInstanceNodeStatus.CREATED,
    output = null,
    error = null,
)
