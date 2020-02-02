package net.nemerosa.ontrack.boot

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventListener
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.ui.controller.URIBuilder
import net.nemerosa.ontrack.ui.support.AbstractSearchProvider
import org.springframework.stereotype.Component
import java.util.regex.Pattern

@Component
class BranchSearchProvider(
        uriBuilder: URIBuilder,
        private val structureService: StructureService,
        private val searchIndexService: SearchIndexService
) : AbstractSearchProvider(uriBuilder), SearchIndexer<BranchSearchItem>, EventListener {

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
                                100.0
                        )
                    }

    override fun getSearchIndexers(): Collection<SearchIndexer<*>> = listOf(this)

    override val indexerName: String = "Branches"

    override val indexName: String = BRANCH_SEARCH_INDEX

    override fun indexAll(processor: (BranchSearchItem) -> Unit) {
        structureService.projectList.forEach {
            structureService.getBranchesForProject(it.id).forEach { branch ->
                processor(branch.asSearchItem())
            }
        }
    }

    override fun toSearchResult(id: String, score: Double, source: JsonNode): SearchResult? =
            structureService.findBranchByID(ID.of(id.toInt()))?.run {
                SearchResult(
                        entityDisplayName,
                        description ?: "",
                        uriBuilder.getEntityURI(this),
                        uriBuilder.getEntityPage(this),
                        score
                )
            }

    override fun onEvent(event: Event) {
        when (event.eventType) {
            EventFactory.NEW_BRANCH -> {
                val branch = event.getEntity<Branch>(ProjectEntityType.BRANCH)
                searchIndexService.createSearchIndex(this, branch.asSearchItem())
            }
            EventFactory.UPDATE_BRANCH -> {
                val branch = event.getEntity<Branch>(ProjectEntityType.BRANCH)
                searchIndexService.updateSearchIndex(this, branch.asSearchItem())
            }
            EventFactory.DELETE_BRANCH -> {
                val branchId = event.getIntValue("branch_id")
                searchIndexService.deleteSearchIndex(this, branchId)
            }
        }
    }

    private fun Branch.asSearchItem() = BranchSearchItem(this)
}

/**
 * Index name for the branches
 */
const val BRANCH_SEARCH_INDEX = "branches"

class BranchSearchItem(
        override val id: String,
        val name: String,
        val description: String,
        val project: String
) : SearchItem {

    constructor(branch: Branch) : this(
            id = branch.id.toString(),
            name = branch.name,
            description = branch.description ?: "",
            project = branch.project.name
    )

    override val fields: Map<String, Any> = mapOf(
            "name" to name,
            "description" to description,
            "project" to project
    )
}