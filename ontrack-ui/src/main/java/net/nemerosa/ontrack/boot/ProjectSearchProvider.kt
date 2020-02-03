package net.nemerosa.ontrack.boot

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventListener
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.ui.controller.URIBuilder
import net.nemerosa.ontrack.ui.support.AbstractSearchProvider
import org.springframework.stereotype.Component
import java.util.regex.Pattern

/**
 * Minimum length of a token to compare with project names by content.
 */
const val PROJECT_SEARCH_PROVIDER_TOKEN_MIN_LENGTH = 3

/**
 * Projects search index name
 */
const val PROJECT_SEARCH_INDEX = "projects"

/**
 * Search result type
 */
const val PROJECT_SEARCH_RESULT_TYPE = "project"

/**
 * Matches against projects either by name or by content if the
 * search token is at least [PROJECT_SEARCH_PROVIDER_TOKEN_MIN_LENGTH] characters long.
 */
@Component
class ProjectSearchProvider(
        uriBuilder: URIBuilder,
        private val structureService: StructureService,
        private val searchIndexService: SearchIndexService
) : AbstractSearchProvider(uriBuilder), SearchIndexer<ProjectSearchItem>, EventListener {

    override val searchResultType = SearchResultType(
            feature = CoreExtensionFeature.INSTANCE.featureDescription,
            id = PROJECT_SEARCH_RESULT_TYPE,
            name = "Project"
    )

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
                            title = project.entityDisplayName,
                            description = "",
                            uri = uriBuilder.getEntityURI(project),
                            page = uriBuilder.getEntityPage(project),
                            accuracy = 100.0,
                            type = searchResultType
                    )
                }
    }

    override fun getSearchIndexers(): Collection<SearchIndexer<*>> = listOf(this)

    override val indexerName: String = "Projects"

    override val indexName: String = PROJECT_SEARCH_INDEX

    override val indexMapping: SearchIndexMapping = indexMappings<ProjectSearchItem> {
        +ProjectSearchItem::name to keyword { scoreBoost = 3.0 }
        +ProjectSearchItem::description to text()
    }

    override fun indexAll(processor: (ProjectSearchItem) -> Unit) {
        structureService.projectList.forEach { project ->
            processor(project.asSearchItem())
        }
    }

    override fun toSearchResult(id: String, score: Double, source: JsonNode): SearchResult? {
        return structureService.findProjectByID(ID.of(id.toInt()))?.run {
            SearchResult(
                    title = entityDisplayName,
                    description = description ?: "",
                    uri = uriBuilder.getEntityURI(this),
                    page = uriBuilder.getEntityPage(this),
                    accuracy = score,
                    type = searchResultType
            )
        }
    }

    override fun onEvent(event: Event) {
        when (event.eventType) {
            EventFactory.NEW_PROJECT -> {
                val project = event.getEntity<Project>(ProjectEntityType.PROJECT)
                searchIndexService.createSearchIndex(this, project.asSearchItem())
            }
            EventFactory.UPDATE_PROJECT -> {
                val project = event.getEntity<Project>(ProjectEntityType.PROJECT)
                searchIndexService.updateSearchIndex(this, project.asSearchItem())
            }
            EventFactory.DELETE_PROJECT -> {
                val projectId = event.getIntValue("project_id")
                searchIndexService.deleteSearchIndex(this, projectId)
            }
        }
    }

    private fun Project.asSearchItem() = ProjectSearchItem(this)

}

class ProjectSearchItem(
        override val id: String,
        val name: String,
        val description: String
) : SearchItem {
    constructor(project: Project) : this(
            id = project.id.toString(),
            name = project.name,
            description = project.description ?: ""
    )

    override val fields: Map<String, Any> = mapOf(
            "name" to name,
            "description" to description
    )

}
