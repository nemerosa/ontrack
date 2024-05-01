package net.nemerosa.ontrack.kdsl.spec.extension.workflows

data class WorkflowInstance(
    val status: WorkflowInstanceStatus,
    val finished: Boolean,
)
