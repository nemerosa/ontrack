package net.nemerosa.ontrack.service.templating

import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.templating.TemplatingFunction
import net.nemerosa.ontrack.model.templating.getRequiredTemplatingParam
import org.springframework.stereotype.Component

@Component
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