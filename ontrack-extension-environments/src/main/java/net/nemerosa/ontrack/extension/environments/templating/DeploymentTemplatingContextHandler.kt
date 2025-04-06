package net.nemerosa.ontrack.extension.environments.templating

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
    deploymentTemplatingContextFieldHandlers: List<DeploymentTemplatingContextFieldHandler>,
) : AbstractTemplatingContextHandler<DeploymentTemplatingContextData>(
    DeploymentTemplatingContextData::class
) {

    private val fieldHandlers = deploymentTemplatingContextFieldHandlers.associateBy { it.field }

    override val id: String = "deployment"

    override fun render(
        data: DeploymentTemplatingContextData,
        field: String?,
        config: Map<String, String>,
        renderer: EventRenderer
    ): String {
        // Loading the deployment
        val deployment = slotPipelineRepository.getPipelineById(data.slotPipelineId)
        // Gets the field name
        val fieldName = field?.takeIf { it.isNotBlank() } ?: DeploymentTemplatingContextFieldHandler.DEFAULT_FIELD
        // Field handler
        val fieldHandler = fieldHandlers[fieldName]
            ?: throw TemplatingContextHandlerFieldNotManagedException(this, field)
        // Rendering
        return fieldHandler.render(
            deployment = deployment,
            config = config,
            renderer = renderer,
        )
    }

}