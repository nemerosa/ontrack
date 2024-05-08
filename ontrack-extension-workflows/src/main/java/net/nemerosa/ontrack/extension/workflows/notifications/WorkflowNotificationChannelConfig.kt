package net.nemerosa.ontrack.extension.workflows.notifications

import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.DocumentationField

data class WorkflowNotificationChannelConfig(
    @APIDescription("Workflow to run")
    @DocumentationField
    val workflow: Workflow,
)
