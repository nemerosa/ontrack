package net.nemerosa.ontrack.boot

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.ui.controller.URIBuilder
import net.nemerosa.ontrack.ui.support.AbstractSearchProvider
import org.springframework.stereotype.Component
import java.util.regex.Pattern

@Component
class BranchSearchProvider(
        uriBuilder: URIBuilder,
        private val structureService: StructureService
) : AbstractSearchProvider(uriBuilder), SearchIndexer<BranchSearchItem> {

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

    override fun getSearchIndexers(): Collection<SearchIndexer<*>> = listOf(this)

    override val indexerName: String = "Branches"

    override val indexName: String = "branches"

    override fun indexation(): Sequence<BranchSearchItem> =
            structureService.projectList
                    .asSequence()
                    .flatMap {
                        structureService.getBranchesForProject(it.id)
                                .asSequence()
                                .map { branch -> BranchSearchItem(branch) }
                    }
}

class BranchSearchItem(branch: Branch) : SearchItem {
    override val id: String = branch.id.toString()

    override val fields: Map<String, Any> = mapOf(
            "name" to branch.name,
            "description" to branch.description
    )
}