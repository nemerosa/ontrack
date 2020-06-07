package net.nemerosa.ontrack.extension.git

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.asMap
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
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.ui.controller.URIBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
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
        private val ontrackConfigProperties: OntrackConfigProperties,
        private val gitIssueSearchExtension: GitIssueSearchExtension
) : AbstractExtension(extensionFeature), SearchIndexer<GitCommitSearchItem> {

    companion object {
        const val GIT_COMMIT_SEARCH_RESULT_TYPE = "git-commit"
        const val GIT_COMMIT_SEARCH_RESULT_DATA_PROJECT = "project"
    }

    private val logger: Logger = LoggerFactory.getLogger(GitCommitSearchExtension::class.java)

    private val shaPattern = Pattern.compile("[a-f0-9]{40}|[a-f0-9]{7}")

    override val searchResultType = SearchResultType(
            extensionFeature.featureDescription,
            GIT_COMMIT_SEARCH_RESULT_TYPE,
            "Git Commit",
            "Commit hash (abbreviated or not)"
    )

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
        logger.info("[search][indexation][git-commits] Indexing all Git commits")
        val traceCommits = ontrackConfigProperties.search.index.logging &&
                ontrackConfigProperties.search.index.tracing &&
                logger.isDebugEnabled
        gitService.forEachConfiguredProject(BiConsumer { project, gitConfiguration ->
            logger.info("[search][indexation][git-commits] project=${project.name}")
            val issueConfig: ConfiguredIssueService? = gitConfiguration.configuredIssueService.orElse(null)
            val projectIssueKeys = mutableSetOf<String>()
            if (gitService.isRepositorySynched(gitConfiguration)) {
                logger.info("[search][indexation][git-commits] project=${project.name} Git repository is synched. Indexing all commits...")
                var commitCount = 0
                gitService.forEachCommit(gitConfiguration) { commit: GitCommit ->
                    commitCount++
                    // Logging
                    if (traceCommits) {
                        logger.info("[search][indexation][git-commits] project=${project.name} commit=${commit.shortId} message=${commit.shortMessage}")
                    }
                    // Indexation of the message
                    val item = GitCommitSearchItem(project, gitConfiguration, commit)
                    processor(item)
                    // Gets the list of issues
                    if (issueConfig != null) {
                        val keys = issueConfig.extractIssueKeysFromMessage(commit.fullMessage)
                        projectIssueKeys.addAll(keys)
                    }
                }
                logger.info("[search][indexation][git-commits] project=${project.name} count=$commitCount commits indexed.")
            } else {
                logger.info("[search][indexation][git-commits] project=${project.name} Git repository is not synched. Not indexing any commit.")
            }
            // Processing of issues
            if (issueConfig != null && projectIssueKeys.isNotEmpty()) {
                logger.info("[search][indexation][git-commits] project=${project.name} issues=${projectIssueKeys.size} Git issues have been found.")
                gitIssueSearchExtension.processIssueKeys(project, issueConfig, projectIssueKeys)
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
                    type = searchResultType,
                    data = mapOf(
                            GIT_COMMIT_SEARCH_RESULT_DATA_PROJECT to project,
                            SearchResult.SEARCH_RESULT_ITEM to item
                    )
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