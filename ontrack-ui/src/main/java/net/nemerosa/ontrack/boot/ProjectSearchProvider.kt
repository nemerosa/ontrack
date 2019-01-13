package net.nemerosa.ontrack.boot

import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.SearchResult
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.ui.controller.URIBuilder
import net.nemerosa.ontrack.ui.support.AbstractSearchProvider
import org.springframework.stereotype.Component
import java.util.regex.Pattern

/**
 * Minimum length of a token to compare with project names by content.
 */
const val PROJECT_SEARCH_PROVIDER_TOKEN_MIN_LENGTH = 3

/**
 * Matches against projects either by name or by content if the
 * search token is at least [PROJECT_SEARCH_PROVIDER_TOKEN_MIN_LENGTH] characters long.
 */
@Component
class ProjectSearchProvider(
        uriBuilder: URIBuilder,
        private val structureService: StructureService
) : AbstractSearchProvider(uriBuilder) {

    override fun isTokenSearchable(token: String): Boolean {
        return Pattern.matches(NameDescription.NAME, token)
    }

    override fun search(token: String): Collection<SearchResult> {
        return structureService.projectList
                .filter { project ->
                    project.name.equals(token, true) ||
                            (token.length >= PROJECT_SEARCH_PROVIDER_TOKEN_MIN_LENGTH &&
                                    project.name.contains(token, true)
                                    )
                }
                .map { project ->
                    SearchResult(
                            project.entityDisplayName,
                            "",
                            uriBuilder.getEntityURI(project),
                            uriBuilder.getEntityPage(project),
                            100
                    )
                }
    }
}
