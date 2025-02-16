package net.nemerosa.ontrack.extension.environments.templating

import net.nemerosa.ontrack.model.templating.TemplatingContext

data class DeploymentTemplatingContextData(
    val slotPipelineId: String,
) : TemplatingContext
