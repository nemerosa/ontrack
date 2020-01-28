package net.nemerosa.ontrack.boot

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventListener
import net.nemerosa.ontrack.model.exceptions.BuildNotFoundException
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.ui.controller.URIBuilder
import net.nemerosa.ontrack.ui.support.AbstractSearchProvider
import org.springframework.stereotype.Component
import java.util.regex.Pattern

@Component
class BuildSearchProvider(
        uriBuilder: URIBuilder,
        private val structureService: StructureService,
        private val searchIndexService: SearchIndexService
) : AbstractSearchProvider(uriBuilder), SearchIndexer<BuildSearchItem>, EventListener {

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

    override fun getSearchIndexers(): Collection<SearchIndexer<*>> = listOf(this)

    override val indexerName: String = "Builds"
    override val indexName: String = BUILD_SEARCH_INDEX

    override fun indexAll(processor: (BuildSearchItem) -> Unit) {
        structureService.projectList.forEach { project ->
            structureService.getBranchesForProject(project.id).forEach { branch ->
                structureService.forEachBuild(branch, BuildSortDirection.FROM_OLDEST) { build ->
                    processor(BuildSearchItem(build))
                    true // Going on
                }
            }
        }
    }

    override fun toSearchResult(id: String, score: Double, source: JsonNode): SearchResult? {
        return try {
            val build = structureService.getBuild(ID.of(id.toInt()))
            SearchResult(
                    build.entityDisplayName,
                    build.description,
                    uriBuilder.getEntityURI(build),
                    uriBuilder.getEntityPage(build),
                    score
            )
        } catch (_: BuildNotFoundException) {
            null
        }
    }

    override fun onEvent(event: Event) {
        when (event.eventType) {
            EventFactory.NEW_BUILD -> {
                val build = event.getEntity<Build>(ProjectEntityType.BUILD)
                searchIndexService.createSearchIndex(this, BuildSearchItem(build))
            }
            EventFactory.UPDATE_BUILD -> {
                val build = event.getEntity<Build>(ProjectEntityType.BUILD)
                searchIndexService.updateSearchIndex(this, BuildSearchItem(build))
            }
            EventFactory.DELETE_BUILD -> {
                val buildId = event.getIntValue("build_id")
                searchIndexService.deleteSearchIndex(this, buildId)
            }
        }
    }
}

/**
 * Index name for the builds
 */
const val BUILD_SEARCH_INDEX = "builds"

data class BuildSearchItem(
        override val id: String,
        val name: String,
        val description: String
) : SearchItem {
    constructor(build: Build) : this(
            id = build.id().toString(),
            name = build.name,
            description = build.description
    )

    override val fields: Map<String, Any?> = mapOf(
            "name" to name,
            "description" to description
    )

}