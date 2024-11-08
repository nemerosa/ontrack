package net.nemerosa.ontrack.extension.environments.workflows

import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.extension.environments.service.getPipelineById
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.docs.DocumentationExampleCode
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.templating.TemplatingFunction
import net.nemerosa.ontrack.model.templating.getRequiredTemplatingParam
import net.nemerosa.ontrack.ui.controller.UILocations
import org.springframework.stereotype.Component

@Component
@APIDescription("Renders a slot pipeline using its ID")
@Documentation(PipelineTemplatingFunctionParameters::class)
@DocumentationExampleCode(
    """
       #.pipeline?id=workflow.pipeline.targetPipelineId 
    """
)
class PipelineTemplatingFunction(
    private val slotService: SlotService,
    private val uiLocations: UILocations,
    private val securityService: SecurityService,
) : TemplatingFunction {

    override val id: String = "pipeline"

    override fun render(
        configMap: Map<String, String>,
        context: Map<String, Any>,
        renderer: EventRenderer,
        expressionResolver: (expression: String) -> String
    ): String {
        val idExpression = configMap.getRequiredTemplatingParam("id")
        val id = expressionResolver(idExpression)
        val pipeline = securityService.asAdmin {
            slotService.getPipelineById(id)
        }
        val link = uiLocations.page("/extension/environments/pipeline/$id")
        return renderer.renderLink(pipeline.fullName(), link)
    }

}