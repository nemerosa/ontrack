package net.nemerosa.ontrack.service.templating

import net.nemerosa.ontrack.model.templating.TemplatingFilter
import org.springframework.stereotype.Component

@Component
class LowercaseTemplatingFilter : TemplatingFilter {

    override val id: String = "lowercase"

    override fun apply(text: String): String = text.lowercase()
}