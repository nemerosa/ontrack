package net.nemerosa.ontrack.boot

import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.SearchResult
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.ui.controller.URIBuilder
import net.nemerosa.ontrack.ui.support.AbstractSearchProvider
import org.springframework.stereotype.Component
import java.util.regex.Pattern

@Component
class ProjectSearchProvider(
        uriBuilder: URIBuilder,
        private val structureService: StructureService
) : AbstractSearchProvider(uriBuilder) {

    override fun isTokenSearchable(token: String): Boolean {
        return Pattern.matches(NameDescription.NAME, token)
    }

    override fun search(token: String): Collection<SearchResult> {
        val oProject = structureService.findProjectByName(token)
        return if (oProject.isPresent) {
            val project = oProject.get()
            listOf(SearchResult(
                    project.entityDisplayName,
                    "",
                    uriBuilder.getEntityURI(project),
                    uriBuilder.getEntityPage(project),
                    100
            ))
        } else {
            emptyList()
        }
    }
}
