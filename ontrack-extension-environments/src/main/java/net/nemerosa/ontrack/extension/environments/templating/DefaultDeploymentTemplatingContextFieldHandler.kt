package net.nemerosa.ontrack.extension.environments.templating

import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.events.EventRenderer
import org.springframework.stereotype.Component

@Component
@APIDescription("Displays a link to the deployment (the field name can be omitted)")
class DefaultDeploymentTemplatingContextFieldHandler(
    private val linkDeploymentTemplatingContextFieldHandler: LinkDeploymentTemplatingContextFieldHandler,
) : DeploymentTemplatingContextFieldHandler {

    override val field: String = DeploymentTemplatingContextFieldHandler.DEFAULT_FIELD

    override fun render(deployment: SlotPipeline, config: Map<String, String>, renderer: EventRenderer): String =
        linkDeploymentTemplatingContextFieldHandler.render(deployment, config, renderer)

}