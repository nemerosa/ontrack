package net.nemerosa.ontrack.service.templating

import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.templating.TemplatingFilter
import org.springframework.stereotype.Component

@Component
class UppercaseTemplatingFilter: TemplatingFilter {

    override val id: String = "uppercase"

    override fun apply(text: String, renderer: EventRenderer): String = text.uppercase()
}