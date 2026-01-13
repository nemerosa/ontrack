package net.nemerosa.ontrack.extension.environments.templating

import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.templating.TemplatingSourceConfig
import org.springframework.stereotype.Component

@Component
@APIDescription("Displays the name of the deployment")
class NameDeploymentTemplatingContextFieldHandler : DeploymentTemplatingContextFieldHandler {

    override val field: String = "name"

    override fun render(deployment: SlotPipeline, config: TemplatingSourceConfig, renderer: EventRenderer): String {
        return deployment.fullName()
    }
}
