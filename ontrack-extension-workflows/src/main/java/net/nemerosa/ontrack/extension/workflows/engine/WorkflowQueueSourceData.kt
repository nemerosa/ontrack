package net.nemerosa.ontrack.extension.workflows.engine

data class WorkflowQueueSourceData(
    val workflowInstanceId: String,
    val workflowNodeId: String,
    val workflowNodeExecutorId: String,
)
