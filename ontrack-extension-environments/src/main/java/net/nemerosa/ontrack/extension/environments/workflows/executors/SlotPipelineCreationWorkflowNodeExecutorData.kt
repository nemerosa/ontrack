package net.nemerosa.ontrack.extension.environments.workflows.executors

data class SlotPipelineCreationWorkflowNodeExecutorData(
    val environment: String,
    val qualifier: String? = null,
)
