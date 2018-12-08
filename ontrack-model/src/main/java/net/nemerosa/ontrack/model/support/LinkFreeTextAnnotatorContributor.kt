package net.nemerosa.ontrack.model.support

import net.nemerosa.ontrack.model.structure.ProjectEntity
import org.springframework.stereotype.Component
import java.util.regex.Pattern

/**
 * Provides an annotator which transforms any HTTP link
 * into an actual link.
 */
@Component
class LinkFreeTextAnnotatorContributor : FreeTextAnnotatorContributor {

    private val pattern = Pattern.compile("((https?://|ftp://|www\\.)\\S+)")

    override fun getMessageAnnotator(entity: ProjectEntity) =
            RegexMessageAnnotator(
                    pattern
            ) { link ->
                MessageAnnotation.of("a")
                        .attr("href", link)
                        .attr("target", "_blank")
                        .text(link)
            }
}