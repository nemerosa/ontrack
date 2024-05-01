package net.nemerosa.ontrack.extension.workflows.notifications

data class WorkflowNotificationChannelConfig(
    val name: String,
    val nodes: List<WorkflowNotificationChannelConfigNode>,
)
