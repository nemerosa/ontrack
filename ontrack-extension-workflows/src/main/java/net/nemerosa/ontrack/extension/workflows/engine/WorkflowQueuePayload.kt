package net.nemerosa.ontrack.extension.workflows.engine

data class WorkflowQueuePayload(
    val workflowNodeExecutorId: String,
    val workflowInstanceId: String,
    val workflowNodeId: String,
)
