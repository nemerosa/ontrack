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
class BuildSearchProvider(
        uriBuilder: URIBuilder,
        private val structureService: StructureService,
        private val searchIndexService: SearchIndexService
) : AbstractSearchProvider(uriBuilder), SearchIndexer<BuildSearchItem>, EventListener {

    override val searchResultType = SearchResultType(
            feature = CoreExtensionFeature.INSTANCE.featureDescription,
            id = BUILD_SEARCH_RESULT_TYPE,
            name = "Build"
    )

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
                            title = build.entityDisplayName,
                            description = "",
                            uri = uriBuilder.getEntityURI(build),
                            page = uriBuilder.getEntityPage(build),
                            accuracy = 100.0,
                            type = searchResultType
                    )
                }
    }

    override fun getSearchIndexers(): Collection<SearchIndexer<*>> = listOf(this)

    override val indexerName: String = "Builds"
    override val indexName: String = BUILD_SEARCH_INDEX

    override val indexMapping: SearchIndexMapping = indexMappings<BuildSearchItem> {
        +BuildSearchItem::name to keyword { scoreBoost = 3.0 }
        +BuildSearchItem::description to text()
    }

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

    override fun toSearchResult(id: String, score: Double, source: JsonNode): SearchResult? =
            structureService.findBuildByID(ID.of(id.toInt()))?.run {
                SearchResult(
                        title = entityDisplayName,
                        description = description ?: "",
                        uri = uriBuilder.getEntityURI(this),
                        page = uriBuilder.getEntityPage(this),
                        accuracy = score,
                        type = searchResultType
                )
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

/**
 * Search result type
 */
const val BUILD_SEARCH_RESULT_TYPE = "build"

data class BuildSearchItem(
        override val id: String,
        val name: String,
        val description: String
) : SearchItem {
    constructor(build: Build) : this(
            id = build.id().toString(),
            name = build.name,
            description = build.description ?: ""
    )

    override val fields: Map<String, Any?> = mapOf(
            "name" to name,
            "description" to description
    )

}