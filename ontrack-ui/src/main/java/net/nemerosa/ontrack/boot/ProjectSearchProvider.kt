package net.nemerosa.ontrack.boot

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.support.CoreExtensionFeature
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventListener
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component

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
    private val structureService: StructureService,
    private val searchIndexService: SearchIndexService
) : SearchIndexer<ProjectSearchItem>, EventListener {

    override val searchResultType = SearchResultType(
        feature = CoreExtensionFeature.INSTANCE.featureDescription,
        id = PROJECT_SEARCH_RESULT_TYPE,
        name = "Project",
        description = "Project name in Ontrack",
        order = SearchResultType.ORDER_PROJECT,
    )

    override val indexerName: String = "Projects"

    override val indexName: String = PROJECT_SEARCH_INDEX

    override val indexSettings = SearchIndexSettings(
        analyzers = mapOf(
            "autocomplete" to AnalyzerConfig(
                tokenizer = "autocomplete_tokenizer",
                filters = listOf("lowercase")
            ),
            "autocomplete_search" to AnalyzerConfig(
                tokenizer = "lowercase"
            )
        ),
        tokenizers = mapOf(
            "autocomplete_tokenizer" to TokenizerConfig(
                type = "edge_ngram",
                minGram = 3,
                maxGram = 10,
                tokenChars = listOf("letter", "digit")
            )
        )
    )

    override val indexMapping: SearchIndexMapping = indexMappings<ProjectSearchItem> {
        +ProjectSearchItem::name to text {
            scoreBoost = 3.0
            analyzer = "autocomplete"
            searchAnalyzer = "autocomplete_search"
        }
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
                accuracy = score,
                type = searchResultType,
                data = mapOf(
                    SearchResult.SEARCH_RESULT_PROJECT to this,
                )
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
                val projectId = event.getIntValue("PROJECT_ID")
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
