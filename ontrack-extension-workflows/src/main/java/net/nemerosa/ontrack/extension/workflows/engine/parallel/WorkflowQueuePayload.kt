package net.nemerosa.ontrack.extension.workflows.engine.parallel

data class WorkflowQueuePayload(
    val workflowInstanceId: String,
    val workflowNodeId: String,
)
