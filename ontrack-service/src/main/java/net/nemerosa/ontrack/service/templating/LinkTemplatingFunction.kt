package net.nemerosa.ontrack.service.templating

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.docs.DocumentationExampleCode
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.templating.TemplatingFunction
import net.nemerosa.ontrack.model.templating.getRequiredTemplatingParam
import org.springframework.stereotype.Component

@Component
@APIDescription("Creates a link")
@Documentation(LinkTemplatingFunctionParameters::class)
@DocumentationExampleCode(
    """
       #.link?text=PR_TITLE&href=PR_LINK 
    """
)
class LinkTemplatingFunction : TemplatingFunction {

    override fun render(
        configMap: Map<String, String>,
        context: Map<String, Any>,
        renderer: EventRenderer,
        expressionResolver: (expression: String) -> String,
    ): String {
        val textExpression = configMap.getRequiredTemplatingParam("text")
        val hrefExpression = configMap.getRequiredTemplatingParam("href")
        val text = expressionResolver(textExpression)
        val href = expressionResolver(hrefExpression)
        return renderer.renderLink(text, href)
    }

    override val id: String = "link"
}