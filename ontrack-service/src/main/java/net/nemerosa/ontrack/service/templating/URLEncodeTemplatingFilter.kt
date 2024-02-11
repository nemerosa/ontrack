package net.nemerosa.ontrack.service.templating

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.DocumentationExampleCode
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.templating.TemplatingFilter
import org.springframework.stereotype.Component
import java.net.URLEncoder

@Component
@APIDescription("URL encoding of the value")
@DocumentationExampleCode("${'$'}{branch.scmBranch|urlencode}")
class URLEncodeTemplatingFilter : TemplatingFilter {

    override val id: String = "urlencode"

    override fun apply(text: String, renderer: EventRenderer): String = URLEncoder.encode(text, Charsets.UTF_8)
}