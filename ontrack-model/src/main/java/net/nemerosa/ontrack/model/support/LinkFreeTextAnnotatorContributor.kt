package net.nemerosa.ontrack.model.support

import org.springframework.stereotype.Component
import java.util.regex.Pattern

/**
 * Provides an annotator which transforms any HTTP link
 * into an actual link.
 */
@Component
class LinkFreeTextAnnotatorContributor : FreeTextAnnotatorContributor {

    private val pattern = Pattern.compile("((https?:\\/\\/|ftp:\\/\\/|www\\.)\\S+)")

    override val messageAnnotator: MessageAnnotator
        get() =
            RegexMessageAnnotator(
                    pattern
            ) { link ->
                MessageAnnotation.of("a")
                        .attr("href", link)
                        .attr("target", "_blank")
                        .text(link)
            }
}