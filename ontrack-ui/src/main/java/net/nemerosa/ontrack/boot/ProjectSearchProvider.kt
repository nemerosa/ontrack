package net.nemerosa.ontrack.boot

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventListener
import net.nemerosa.ontrack.model.exceptions.ProjectNotFoundException
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
 * Matches against projects either by name or by content if the
 * search token is at least [PROJECT_SEARCH_PROVIDER_TOKEN_MIN_LENGTH] characters long.
 */
@Component
class ProjectSearchProvider(
        uriBuilder: URIBuilder,
        private val structureService: StructureService,
        private val searchIndexService: SearchIndexService
) : AbstractSearchProvider(uriBuilder), SearchIndexer<ProjectSearchItem>, EventListener {

    companion object {
        const val PROJECTS_INDEX = "projects"
    }

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
                            100.0
                    )
                }
    }

    override fun getSearchIndexers(): Collection<SearchIndexer<*>> = listOf(this)

    override val indexerName: String = "Projects"

    override val indexName: String = PROJECTS_INDEX

    override fun indexAll(processor: (ProjectSearchItem) -> Unit) {
        structureService.projectList.forEach { project ->
            processor(project.asSearchItem())
        }
    }

    override fun toSearchResult(id: String, score: Double, source: JsonNode): SearchResult? {
        return try {
            val project = structureService.getProject(ID.of(id.toInt()))
            SearchResult(
                    project.name,
                    project.entityDisplayName,
                    uriBuilder.getEntityURI(project),
                    uriBuilder.getEntityPage(project),
                    score
            )
        } catch (_: ProjectNotFoundException) {
            null
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

class ProjectSearchItem(project: Project) : SearchItem {
    override val id: String = project.id.toString()

    override val fields: Map<String, Any> = mapOf(
            "name" to project.name,
            "description" to project.description
    )

}
