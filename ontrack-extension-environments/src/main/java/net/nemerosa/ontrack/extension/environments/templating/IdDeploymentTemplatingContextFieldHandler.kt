package net.nemerosa.ontrack.extension.environments.templating

import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.events.EventRenderer
import org.springframework.stereotype.Component

@Component
@APIDescription("Displays the ID of the deployment")
class IdDeploymentTemplatingContextFieldHandler : DeploymentTemplatingContextFieldHandler {

    override val field: String = "id"

    override fun render(deployment: SlotPipeline, config: Map<String, String>, renderer: EventRenderer): String {
        return deployment.id
    }
}
