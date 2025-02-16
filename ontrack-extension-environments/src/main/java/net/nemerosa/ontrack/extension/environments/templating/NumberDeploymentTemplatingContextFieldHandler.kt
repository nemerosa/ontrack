package net.nemerosa.ontrack.extension.environments.templating

import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.events.EventRenderer
import org.springframework.stereotype.Component

@Component
@APIDescription("Displays the number of the deployment")
class NumberDeploymentTemplatingContextFieldHandler : DeploymentTemplatingContextFieldHandler {

    override val field: String = "number"

    override fun render(deployment: SlotPipeline, config: Map<String, String>, renderer: EventRenderer): String {
        return deployment.number.toString()
    }
}
