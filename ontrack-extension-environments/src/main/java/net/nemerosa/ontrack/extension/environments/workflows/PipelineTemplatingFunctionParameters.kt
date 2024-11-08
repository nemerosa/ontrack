package net.nemerosa.ontrack.extension.environments.workflows

import net.nemerosa.ontrack.model.annotations.APIDescription

data class PipelineTemplatingFunctionParameters(
    @APIDescription("ID of the slot pipeline")
    val id: String,
)
