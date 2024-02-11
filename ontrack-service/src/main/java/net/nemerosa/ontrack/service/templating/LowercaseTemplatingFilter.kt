package net.nemerosa.ontrack.service.templating

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.DocumentationExampleCode
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.templating.TemplatingFilter
import org.springframework.stereotype.Component

@Component
@APIDescription("Making a value lower case")
@DocumentationExampleCode("${'$'}{project|lowercase}")
class LowercaseTemplatingFilter : TemplatingFilter {

    override val id: String = "lowercase"

    override fun apply(text: String, renderer: EventRenderer): String = text.lowercase()
}