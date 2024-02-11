package net.nemerosa.ontrack.service.templating

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.DocumentationExampleCode
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.templating.TemplatingFilter
import org.springframework.stereotype.Component

/**
 * Renders a value with some emphasis (strong or bold).
 */
@Component
@APIDescription("Making a value in a stronger typography")
@DocumentationExampleCode("${'$'}{VALUE|string}")
class StrongTemplatingFilter : TemplatingFilter {

    override val id: String = "strong"

    override fun apply(text: String, renderer: EventRenderer): String =
        renderer.renderStrong(text)
}