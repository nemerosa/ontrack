package net.nemerosa.ontrack.extension.environments.workflows

import net.nemerosa.ontrack.extension.environments.events.EnvironmentsEvents
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.docs.DocumentationExampleCode
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.templating.TemplatingFunction
import net.nemerosa.ontrack.ui.controller.UILocations
import org.springframework.stereotype.Component

@Component
@APIDescription("Renders a slot using its ID")
@Documentation(SlotTemplatingFunctionParameters::class)
@DocumentationExampleCode(
    """
       #.slot 
    """
)
class SlotTemplatingFunction(
    private val slotService: SlotService,
    private val uiLocations: UILocations,
) : TemplatingFunction {

    override val id: String = "slot"

    override fun render(
        configMap: Map<String, String>,
        context: Map<String, Any>,
        renderer: EventRenderer,
        expressionResolver: (expression: String) -> String
    ): String {
        val idExpression = configMap[SlotTemplatingFunctionParameters::id.name]
        val id = expressionResolver(idExpression ?: EnvironmentsEvents.EVENT_SLOT_ID)
        val slot = slotService.getSlotById(id)
        val link = uiLocations.page("/extension/environments/slot/$id")
        return renderer.renderLink(slot.fullName(), link)
    }

}