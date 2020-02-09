package net.nemerosa.ontrack.extension.git

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.asMap
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.api.SearchExtension
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.job.Schedule
import net.nemerosa.ontrack.json.parseOrNull
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.ui.controller.URIBuilder
import net.nemerosa.ontrack.ui.support.AbstractSearchProvider
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on
import java.util.*
import java.util.function.BiConsumer

@Component
class GitIssueSearchExtension(
        extensionFeature: GitExtensionFeature,
        private val gitService: GitService,
        private val uriBuilder: URIBuilder,
        private val structureService: StructureService
) : AbstractExtension(extensionFeature), SearchExtension, SearchIndexer<GitIssueSearchItem> {

    override fun getSearchProvider(): SearchProvider {
        return GitIssueSearchProvider(uriBuilder)
    }

    protected inner class GitIssueSearchProvider(uriBuilder: URIBuilder) : AbstractSearchProvider(uriBuilder) {

        override fun isTokenSearchable(token: String): Boolean {
            var match = false
            // For all Git-configured projects
            gitService.forEachConfiguredProject(BiConsumer { _, gitConfiguration ->
                if (!match) {
                    // Gets issue service
                    if (gitConfiguration.configuredIssueService.isPresent) {
                        match = gitConfiguration.configuredIssueService.get()
                                .issueServiceExtension.validIssueToken(token)
                    }
                }
            })
            return match
        }

        override fun search(token: String): Collection<SearchResult> {
            // Map of results per project, with the first result being the one for the first corresponding branch
            val projectResults = LinkedHashMap<ID, SearchResult>()
            // For all Git-configured projects
            gitService.forEachConfiguredProject(BiConsumer { project, gitConfiguration ->
                val configuredIssueService: ConfiguredIssueService? =
                        gitConfiguration.configuredIssueService.orElse(null)
                if (configuredIssueService != null) {
                    val projectId: ID = project.id
                    // Skipping if associated project is already associated with the issue
                    if (!projectResults.containsKey(projectId)) {
                        // ... searches for the issue token in the git repository
                        val found = configuredIssueService.issueServiceExtension.validIssueToken(token) &&
                                gitService.isPatternFound(gitConfiguration, token)
                        // ... and if found
                        if (found) {
                            // ... loads the issue
                            val issue: Issue? = configuredIssueService.getIssue(token)
                            // Saves the result for the project if an issue has been found
                            if (issue != null) {
                                projectResults[projectId] = SearchResult(
                                        issue.displayKey,
                                        String.format("Issue %s found in project %s",
                                                issue.key,
                                                project.name
                                        ),
                                        uri(on(GitController::class.java).issueProjectInfo(
                                                project.id,
                                                issue.key
                                        )),
                                        uriBuilder.page("extension/git/%d/issue/%s",
                                                project.id(),
                                                issue.key),
                                        100.0,
                                        searchResultType
                                )
                            }
                        }
                    }
                }
            })
            // OK
            return projectResults.values
        }

    }

    override val indexerName: String = "Git Issues"

    override val indexName: String = GIT_ISSUE_SEARCH_INDEX

    /**
     * Done in the context of [GitCommitSearchExtension.indexAll].
     */
    override val indexerSchedule: Schedule = Schedule.NONE

    override val indexMapping: SearchIndexMapping? = indexMappings<GitIssueSearchItem> {
        +GitIssueSearchItem::projectId to id { index = false }
        +GitIssueSearchItem::key to keyword { scoreBoost = 3.0 }
        +GitIssueSearchItem::summary to text()
    }

    /**
     * Done in the context of [GitCommitSearchExtension.indexAll].
     */
    override fun indexAll(processor: (GitIssueSearchItem) -> Unit) {}

    override val searchResultType = SearchResultType(
            feature = extensionFeature.featureDescription,
            id = "git-issue",
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
                    title = "${item.key} - ${item.summary}",
                    description = "Issue ${item.key} found in project ${project.name}",
                    uri = uriBuilder.build(on(GitController::class.java).issueProjectInfo(project.id, item.key)),
                    page = uriBuilder.page("extension/git/${project.id}/issue/${item.key}"),
                    accuracy = score,
                    type = searchResultType
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
        val summary: String
) : SearchItem {
    override val id: String = "$projectId::$key"

    override val fields: Map<String, Any?> = asMap(
            this::projectId,
            this::key,
            this::summary
    )

}