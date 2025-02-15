package net.nemerosa.ontrack.extension.workflows.graphql

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.templating.TemplatingContextData

@APIDescription("Templating context data associated with a workflow instance")
data class WorkflowInstanceContextData(
    @APIDescription("Name linked to this context")
    val name: String,
    @APIDescription("Context data and reference to its handler")
    val contextData: TemplatingContextData,
)
