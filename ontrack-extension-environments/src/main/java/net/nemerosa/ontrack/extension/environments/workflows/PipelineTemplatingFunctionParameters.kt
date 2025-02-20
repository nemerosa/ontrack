package net.nemerosa.ontrack.extension.environments.workflows

import net.nemerosa.ontrack.extension.environments.events.EnvironmentsEvents
import net.nemerosa.ontrack.model.annotations.APIDescription

data class PipelineTemplatingFunctionParameters(
    @APIDescription("ID of the slot pipeline. Defaults to ${EnvironmentsEvents.EVENT_PIPELINE_ID}")
    val id: String? = null,
)
