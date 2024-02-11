package net.nemerosa.ontrack.service.templating

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.DocumentationExampleCode
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.templating.TemplatingFilter
import org.springframework.stereotype.Component

@Component
@APIDescription("Making a value upper case")
@DocumentationExampleCode("${'$'}{project|uppercase}")
class UppercaseTemplatingFilter: TemplatingFilter {

    override val id: String = "uppercase"

    override fun apply(text: String, renderer: EventRenderer): String = text.uppercase()
}