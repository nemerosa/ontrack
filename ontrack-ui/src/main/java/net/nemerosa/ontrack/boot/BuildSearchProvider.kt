package net.nemerosa.ontrack.boot

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventListener
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.ui.controller.URIBuilder
import org.springframework.stereotype.Component

@Component
class BuildSearchProvider(
        private val uriBuilder: URIBuilder,
        private val structureService: StructureService,
        private val searchIndexService: SearchIndexService
) : SearchIndexer<BuildSearchItem>, EventListener {

    override val searchResultType = SearchResultType(
            feature = CoreExtensionFeature.INSTANCE.featureDescription,
            id = BUILD_SEARCH_RESULT_TYPE,
            name = "Build",
            description = "Build name in Ontrack"
    )

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