package net.nemerosa.ontrack.boot

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.SearchResult
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.ui.controller.URIBuilder
import net.nemerosa.ontrack.ui.support.AbstractSearchProvider
import org.springframework.stereotype.Component
import java.util.regex.Pattern

@Component
class BranchSearchProvider(
        uriBuilder: URIBuilder,
        private val structureService: StructureService
) : AbstractSearchProvider(uriBuilder) {

    override fun isTokenSearchable(token: String): Boolean = Pattern.matches(NameDescription.NAME, token)

    override fun search(token: String): Collection<SearchResult> =
            structureService
                    // Gets the list of authorized projects
                    .projectList
                    // Gets their name
                    .map { it.name }
                    // Looks for a branch
                    .mapNotNull { project: String -> structureService.findBranchByName(project, token).getOrNull() }
                    // Creates the search result
                    .map { branch: Branch ->
                        SearchResult(
                                branch.entityDisplayName,
                                "",
                                uriBuilder.getEntityURI(branch),
                                uriBuilder.getEntityPage(branch),
                                100
                        )
                    }

}