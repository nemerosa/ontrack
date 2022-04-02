package net.nemerosa.ontrack.ui.controller

import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

class AbsoluteURIBuilder(
    ontrackConfigProperties: OntrackConfigProperties,
) : AbstractURIBuilder() {

    private val url = ontrackConfigProperties.url

    override fun build(methodInvocation: Any): URI {
        val builder = MvcUriComponentsBuilder.fromMethodCall(
            UriComponentsBuilder.fromHttpUrl(url),
            methodInvocation
        )
        return builder.build().toUri()
    }

    override fun url(relativeUri: String): URI = concat(url, relativeUri)

    override fun page(path: String, vararg arguments: Any): URI {
        val pagePath = pagePath(path, *arguments)
        return concat(url, pagePath)
    }

    private fun concat(base: String, path: String) =
        URI.create(base.trimEnd('/') + "/" + path.trimStart('/'))
}