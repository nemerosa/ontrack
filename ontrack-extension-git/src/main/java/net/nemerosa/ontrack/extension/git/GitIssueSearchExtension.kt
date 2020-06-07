package net.nemerosa.ontrack.extension.git

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.asMap
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.job.Schedule
import net.nemerosa.ontrack.json.parseOrNull
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.ui.controller.URIBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class GitIssueSearchExtension(
        extensionFeature: GitExtensionFeature,
        private val gitService: GitService,
        private val uriBuilder: URIBuilder,
        private val structureService: StructureService,
        private val ontrackConfigProperties: OntrackConfigProperties,
        private val searchIndexService: SearchIndexService
) : AbstractExtension(extensionFeature), SearchIndexer<GitIssueSearchItem> {

    companion object {
        const val GIT_ISSUE_SEARCH_RESULT_TYPE = "git-issue"
        const val GIT_ISSUE_SEARCH_RESULT_DATA_PROJECT = "project"
    }

    private val logger: Logger = LoggerFactory.getLogger(GitIssueSearchExtension::class.java)


    override val indexerName: String = "Git Issues"

    override val indexName: String = GIT_ISSUE_SEARCH_INDEX

    override val indexerSchedule: Schedule = Schedule.NONE

    override val isIndexationDisabled: Boolean = true

    override val indexMapping: SearchIndexMapping? = indexMappings<GitIssueSearchItem> {
        +GitIssueSearchItem::projectId to id { index = false }
        +GitIssueSearchItem::key to keyword { index = false }
        +GitIssueSearchItem::displayKey to keyword { scoreBoost = 3.0 }
    }

    /**
     * No indexation is needed - it's performed by the [GitCommitSearchExtension].
     *
     * @see processIssueKeys
     */
    override fun indexAll(processor: (GitIssueSearchItem) -> Unit) {}

    fun processIssueKeys(project: Project, issueConfig: ConfiguredIssueService, projectIssueKeys: Set<String>) {
        // Batch size
        val batchSize = ontrackConfigProperties.search.index.batch
        // Split the keys in batches
        val chunks = projectIssueKeys.chunked(batchSize)
        // For each batch
        chunks.forEach { batch ->
            logger.info("[search][indexation][git-issues] project=${project.name} batch=${batch.size} Git issues to index.")
            searchIndexService.batchSearchIndex(
                    indexer = this,
                    items = batch.map { key ->
                        key to issueConfig.getDisplayKey(key)
                    }.map { (key, displayKey) ->
                        GitIssueSearchItem(project, key, displayKey)
                    },
                    mode = BatchIndexMode.KEEP
            )
        }
    }

    override val searchResultType = SearchResultType(
            feature = extensionFeature.featureDescription,
            id = GIT_ISSUE_SEARCH_RESULT_TYPE,
            name = "Git Issue",
            description = "Issue key, as present in Git commit messages"
    )

    override fun toSearchResult(id: String, score: Double, source: JsonNode): SearchResult? {
        // Parsing of item
        val item = source.parseOrNull<GitIssueSearchItem>()
        // Getting the project
        val project = item?.let { structureService.findProjectByID(ID.of(item.projectId)) }
        // Getting the project configuration
        val projectConfig = project?.let { gitService.getProjectConfiguration(project) }
        // Getting the associated issue service
        val issueConfig = projectConfig?.configuredIssueService?.getOrNull()
        // Note: for performances reasons, we don't control if the issue exists or not
        // OK
        return if (project != null && issueConfig != null) {
            SearchResult(
                    title = "Issue ${item.displayKey}",
                    description = "Issue ${item.displayKey} found in project ${project.name}",
                    uri = uriBuilder.build(on(GitController::class.java).issueProjectInfo(project.id, item.key)),
                    page = uriBuilder.page("extension/git/${project.id}/issue/${item.key}"),
                    accuracy = score,
                    type = searchResultType,
                    data = mapOf(
                            GIT_ISSUE_SEARCH_RESULT_DATA_PROJECT to project,
                            SearchResult.SEARCH_RESULT_ITEM to item
                    )
            )
        } else null
    }

}

/**
 * Name of the search index for Git issues
 */
const val GIT_ISSUE_SEARCH_INDEX = "git-issues"

/**
 * Item being indexed for searches on Git issues.
 */
class GitIssueSearchItem(
        val projectId: Int,
        val key: String,
        val displayKey: String
) : SearchItem {

    constructor(project: Project, key: String, displayKey: String) : this(
            projectId = project.id(),
            key = key,
            displayKey = displayKey
    )

    override val id: String = "$projectId::$key"

    override val fields: Map<String, Any?> = asMap(
            this::projectId,
            this::key,
            this::displayKey
    )

}