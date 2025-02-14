package net.nemerosa.ontrack.extension.environments.templating

import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.extension.environments.storage.SlotPipelineRepository
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.templating.AbstractTemplatingContextHandler
import net.nemerosa.ontrack.model.templating.TemplatingContextHandlerFieldNotManagedException
import net.nemerosa.ontrack.ui.controller.UILocations
import org.springframework.stereotype.Component

@Component
class DeploymentTemplatingContextHandler(
    private val slotPipelineRepository: SlotPipelineRepository,
    private val uiLocations: UILocations,
) : AbstractTemplatingContextHandler<DeploymentTemplatingContextData>(
    DeploymentTemplatingContextData::class
) {

    override val id: String = "deployment"

    override fun render(
        data: DeploymentTemplatingContextData,
        field: String?,
        config: Map<String, String>,
        renderer: EventRenderer
    ): String {
        // Loading the deployment
        val deployment = slotPipelineRepository.getPipelineById(data.slotPipelineId)
        // Rendering
        return when (field) {
            null -> renderDeploymentLink(deployment, config, renderer)
            "" -> renderDeploymentLink(deployment, config, renderer)
            "link" -> renderDeploymentLink(deployment, config, renderer)
            "name" -> deployment.fullName()
            "id" -> deployment.id
            "number" -> deployment.number.toString()
            // TODO Changelog
            else -> throw TemplatingContextHandlerFieldNotManagedException(this, field)
        }
    }

    fun renderDeploymentLink(
        deployment: SlotPipeline,
        config: Map<String, String>,
        renderer: EventRenderer
    ): String {
        val link = uiLocations.page("/extension/environments/pipeline/${deployment.id}")
        return renderer.renderLink(deployment.fullName(), link)
    }
}