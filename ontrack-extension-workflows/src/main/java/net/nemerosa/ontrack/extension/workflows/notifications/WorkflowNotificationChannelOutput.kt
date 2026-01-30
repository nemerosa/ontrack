package net.nemerosa.ontrack.extension.workflows.notifications

import net.nemerosa.ontrack.common.api.APIDescription

data class WorkflowNotificationChannelOutput(
    @APIDescription("ID of the workflow instance. Can be used to track the progress and outcome of the workflow.")
    val workflowInstanceId: String,
)
