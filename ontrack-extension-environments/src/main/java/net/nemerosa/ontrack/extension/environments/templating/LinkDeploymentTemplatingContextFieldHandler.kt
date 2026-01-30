package net.nemerosa.ontrack.extension.environments.templating

import net.nemerosa.ontrack.common.api.APIDescription
import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.templating.TemplatingSourceConfig
import net.nemerosa.ontrack.ui.controller.UILocations
import org.springframework.stereotype.Component

@Component
@APIDescription("Displays a link to the deployment")
class LinkDeploymentTemplatingContextFieldHandler(
    val uiLocations: UILocations,
) : DeploymentTemplatingContextFieldHandler {

    override val field: String = "link"

    override fun render(deployment: SlotPipeline, config: TemplatingSourceConfig, renderer: EventRenderer): String {
        val link = uiLocations.page("/extension/environments/pipeline/${deployment.id}")
        return renderer.renderLink(deployment.fullName(), link)
    }
}
