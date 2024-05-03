package net.nemerosa.ontrack.extension.workflows.engine

data class WorkflowQueuePayload(
    val workflowInstanceId: String,
    val workflowNodeId: String,
    val workflowNodeExecutorId: String,
)
