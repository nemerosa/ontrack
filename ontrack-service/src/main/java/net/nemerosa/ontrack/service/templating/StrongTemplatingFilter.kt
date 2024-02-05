package net.nemerosa.ontrack.service.templating

import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.templating.TemplatingFilter
import org.springframework.stereotype.Component

/**
 * Renders a value with some emphasis (strong or bold).
 */
@Component
class StrongTemplatingFilter : TemplatingFilter {

    override val id: String = "strong"

    override fun apply(text: String, renderer: EventRenderer): String =
        renderer.renderStrong(text)
}