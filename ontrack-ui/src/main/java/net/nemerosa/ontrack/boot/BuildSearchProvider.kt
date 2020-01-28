package net.nemerosa.ontrack.boot

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.SearchResult
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.ui.controller.URIBuilder
import net.nemerosa.ontrack.ui.support.AbstractSearchProvider
import org.springframework.stereotype.Component
import java.util.regex.Pattern

@Component
class BuildSearchProvider(
        uriBuilder: URIBuilder,
        private val structureService: StructureService
) : AbstractSearchProvider(uriBuilder) {

    override fun isTokenSearchable(token: String): Boolean {
        return Pattern.matches(NameDescription.NAME, token)
    }

    override fun search(token: String): Collection<SearchResult> {
        return structureService
                // Gets the list of authorized projects
                .projectList
                // Gets the list of branches
                .flatMap { project -> structureService.getBranchesForProject(project.id) }
                // Looks for the builds with the name to search
                .mapNotNull { branch -> structureService.findBuildByName(branch.project.name, branch.name, token).getOrNull() }
                // Creates the search result
                .map { build: Build ->
                    SearchResult(
                            build.entityDisplayName,
                            "",
                            uriBuilder.getEntityURI(build),
                            uriBuilder.getEntityPage(build),
                            100.0
                    )
                }
    }

}