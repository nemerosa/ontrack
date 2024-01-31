package net.nemerosa.ontrack.service.templating

import net.nemerosa.ontrack.model.templating.TemplatingFilter
import org.springframework.stereotype.Component

@Component
class UppercaseTemplatingFilter: TemplatingFilter {

    override val id: String = "uppercase"

    override fun apply(text: String): String = text.uppercase()
}