package net.nemerosa.ontrack.extension.workflows.notifications

import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.DocumentationField
import net.nemerosa.ontrack.model.json.schema.JsonSchemaRef

data class WorkflowNotificationChannelConfig(
    @APIDescription("Workflow to run")
    @DocumentationField
    @JsonSchemaRef("workflow")
    val workflow: Workflow,
    @APIDescription("(used for test only) Short pause before launching the workflow")
    val pauseMs: Long = 0,
)
