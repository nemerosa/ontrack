package net.nemerosa.ontrack.extension.workflows.engine

data class WorkflowInstanceFilter(
    val offset: Int = 0,
    val size: Int = 10,
    val id: String? = null,
    val name: String? = null,
    val status: WorkflowInstanceStatus? = null,
    val triggerId: String? = null,
    val triggerData: String? = null,
)
