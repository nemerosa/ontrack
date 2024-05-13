package net.nemerosa.ontrack.extension.workflows.templating

import net.nemerosa.ontrack.model.annotations.APIDescription

data class WorkflowTemplatingRenderableParameters(
    @APIDescription("JSON path to the data to render")
    val path: String,
)
