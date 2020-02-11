package net.nemerosa.ontrack.extension.git

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.asMap
import net.nemerosa.ontrack.extension.api.SearchExtension
import net.nemerosa.ontrack.extension.git.model.GitConfiguration
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.git.model.GitCommit
import net.nemerosa.ontrack.job.Schedule
import net.nemerosa.ontrack.json.parseOrNull
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.ui.controller.URIBuilder
import net.nemerosa.ontrack.ui.support.AbstractSearchProvider
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import java.util.*
import java.util.function.BiConsumer
import java.util.regex.Pattern

@Component
class GitCommitSearchExtension(
        extensionFeature: GitExtensionFeature,
        private val gitService: GitService,
        private val uriBuilder: URIBuilder,
        private val securityService: SecurityService,
        private val structureService: StructureService,
        gitSearchConfigProperties: GitSearchConfigProperties,
        private val gitIssueSearchExtension: GitIssueSearchExtension
) : AbstractExtension(extensionFeature), SearchExtension, SearchIndexer<GitCommitSearchItem> {

    private val shaPattern = Pattern.compile("[a-f0-9]{40}|[a-f0-9]{7}")

    override val searchResultType = SearchResultType(
            extensionFeature.featureDescription,
            "git-commit",
            "Git Commit",
            "Commit hash (abbreviated or not)"
    )

    override fun getSearchProvider(): SearchProvider {
        return GitCommitSearchProvider(uriBuilder)
    }

    protected inner class GitCommitSearchProvider(uriBuilder: URIBuilder?) : AbstractSearchProvider(uriBuilder) {
        override fun isTokenSearchable(token: String): Boolean {
            return shaPattern.matcher(token).matches()
        }

        override fun search(token: String): Collection<SearchResult> { // List of results
            val results: MutableList<SearchResult> = ArrayList()
            // For all Git-configured projects
            gitService.forEachConfiguredProject(BiConsumer { project: Project, gitConfiguration: GitConfiguration? ->
                // ... scans for the commit
                val commit = gitService.lookupCommit(gitConfiguration!!, token)
                // ... and if found
                if (commit != null) { // ... creates a result entry
                    results.add(
                            SearchResult(String.format("[%s] %s %s",
                                    project.name,
                                    commit.id,
                                    commit.shortMessage), String.format("%s - %s",
                                    commit.author.name,
                                    commit.fullMessage),
                                    uri(MvcUriComponentsBuilder.on(GitController::class.java)
                                            .commitProjectInfo(project.id, commit.id)),
                                    uriBuilder.page("extension/git/%d/commit/%s",
                                            project.id(),
                                            commit.id),
                                    100.0,
                                    searchResultType
                            )
                    )
                }
            })
            // OK
            return results
        }

    }

    override val indexerName: String = "Git commits"

    override val indexName: String = GIT_COMMIT_SEARCH_INDEX

    override val indexerSchedule: Schedule = gitSearchConfigProperties.commits.toSchedule()

    override val indexMapping: SearchIndexMapping? = indexMappings<GitCommitSearchItem> {
        +GitCommitSearchItem::projectId to id { index = false }
        +GitCommitSearchItem::gitType to keyword { index = false }
        +GitCommitSearchItem::gitName to keyword { index = false }
        +GitCommitSearchItem::commit to keyword { scoreBoost = 3.0 }
        +GitCommitSearchItem::commitShort to keyword { scoreBoost = 2.0 }
        +GitCommitSearchItem::commitAuthor to keyword()
        +GitCommitSearchItem::commitMessage to text()
    }

    override fun indexAll(processor: (GitCommitSearchItem) -> Unit) {
        gitService.forEachConfiguredProject(BiConsumer { project, gitConfiguration ->
            val issueConfig: ConfiguredIssueService? = gitConfiguration.configuredIssueService.orElse(null)
            try {
                val projectIssueKeys = mutableSetOf<String>()
                gitService.forEachCommit(gitConfiguration) { commit: GitCommit ->
                    // Indexation of the message
                    val item = GitCommitSearchItem(project, gitConfiguration, commit)
                    processor(item)
                    // Gets the list of issues
                    if (issueConfig != null) {
                        val keys = issueConfig.extractIssueKeysFromMessage(commit.fullMessage)
                        projectIssueKeys.addAll(keys)
                    }
                }
                // Processing of issues
                if (issueConfig != null) {
                    gitIssueSearchExtension.processIssueKeys(project, issueConfig, projectIssueKeys)
                }
            } catch (_: Exception) {
                // Ignoring any error linked to missing indexation
                // They will be caught be the indexation itself
            }
        })
    }

    override fun toSearchResult(id: String, score: Double, source: JsonNode): SearchResult? {
        // Parsing
        val item = source.parseOrNull<GitCommitSearchItem>()
        // Find the project
        val project = item
                ?.let { structureService.findProjectByID(ID.of(item.projectId)) }
                ?.takeIf { securityService.isProjectFunctionGranted(it, ProjectView::class.java) }
        // Conversion
        return if (item != null && project != null) {
            SearchResult(
                    title = "${project.name} ${item.commit}",
                    description = "${item.commitAuthor}: ${item.commitMessage}",
                    uri = uriBuilder.build(
                            MvcUriComponentsBuilder.on(GitController::class.java).commitProjectInfo(
                                    project.id,
                                    item.commit
                            )
                    ),
                    page = uriBuilder.page("extension/git/${project.id}/commit/${item.commit}"),
                    accuracy = score,
                    type = searchResultType
            )
        } else null
    }
}

const val GIT_COMMIT_SEARCH_INDEX = "git-commit"

class GitCommitSearchItem(
        val projectId: Int,
        val gitType: String,
        val gitName: String,
        val commit: String,
        val commitShort: String,
        val commitAuthor: String,
        val commitMessage: String
) : SearchItem {

    constructor(project: Project, gitConfiguration: GitConfiguration, commit: GitCommit) : this(
            projectId = project.id(),
            gitType = gitConfiguration.type,
            gitName = gitConfiguration.name,
            commit = commit.id,
            commitShort = commit.shortId,
            commitAuthor = commit.author.name,
            commitMessage = commit.shortMessage
    )

    override val id: String = "$gitName::$commit"

    override val fields: Map<String, Any?> = asMap(
            this::projectId,
            this::gitType,
            this::gitName,
            this::commit,
            this::commitAuthor,
            this::commitShort,
            this::commitMessage
    )
}