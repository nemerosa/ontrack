package net.nemerosa.ontrack.extension.environments.templating

import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.ui.controller.UILocations
import org.springframework.stereotype.Component

@Component
class LinkDeploymentTemplatingContextFieldHandler(
    val uiLocations: UILocations,
) : DeploymentTemplatingContextFieldHandler {

    override val field: String = "link"

    override fun render(deployment: SlotPipeline, config: Map<String, String>, renderer: EventRenderer): String {
        val link = uiLocations.page("/extension/environments/pipeline/${deployment.id}")
        return renderer.renderLink(deployment.fullName(), link)
    }
}
