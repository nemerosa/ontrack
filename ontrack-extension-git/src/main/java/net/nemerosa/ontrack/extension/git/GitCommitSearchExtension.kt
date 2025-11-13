package net.nemerosa.ontrack.extension.git

import co.elastic.clients.elasticsearch._types.query_dsl.Query
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest
import co.elastic.clients.util.ObjectBuilder
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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class GitCommitSearchExtension(
    extensionFeature: GitExtensionFeature,
    private val gitService: GitService,
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

    override val searchResultType = SearchResultType(
        feature = extensionFeature.featureDescription,
        id = GIT_COMMIT_SEARCH_RESULT_TYPE,
        name = "Git Commit",
        description = "Commit hash (abbreviated or not)",
        order = SearchResultType.ORDER_PROPERTIES + 60,
    )

    override val indexerName: String = "Git commits"

    override val indexName: String = GIT_COMMIT_SEARCH_INDEX

    override val indexerSchedule: Schedule = gitSearchConfigProperties.commits.toSchedule()

    override fun initIndex(builder: CreateIndexRequest.Builder): CreateIndexRequest.Builder =
        builder.run {
            mappings { mappings ->
                mappings
                    .id(GitCommitSearchItem::projectId)
                    .keyword(GitCommitSearchItem::gitType)
                    .keyword(GitCommitSearchItem::gitName)
                    .keyword(GitCommitSearchItem::commit)
                    .keyword(GitCommitSearchItem::commitShort)
                    .keyword(GitCommitSearchItem::commitAuthor)
                    .text(GitCommitSearchItem::commitMessage)
            }
        }

    override fun buildQuery(
        q: Query.Builder,
        token: String
    ): ObjectBuilder<Query> {
        return q.multiMatch { m ->
            m.query(token)
                .type(TextQueryType.BestFields)
                .fields(
                    GitCommitSearchItem::commit to 10.0,
                    GitCommitSearchItem::commitShort to 10.0,
                    GitCommitSearchItem::commitAuthor to 5.0,
                    GitCommitSearchItem::commitMessage to 5.0,
                    GitCommitSearchItem::gitType to 1.0,
                    GitCommitSearchItem::gitName to 1.0,
                )
        }
    }

    override fun indexAll(processor: (GitCommitSearchItem) -> Unit) {
        logger.info("[search][indexation][git-commits] Indexing all Git commits")
        val traceCommits = ontrackConfigProperties.search.index.logging &&
                ontrackConfigProperties.search.index.tracing &&
                logger.isDebugEnabled
        gitService.forEachConfiguredProject { project, gitConfiguration ->
            logger.info("[search][indexation][git-commits] project=${project.name}")
            val issueConfig: ConfiguredIssueService? = gitConfiguration.configuredIssueService
            val projectIssueKeys = mutableSetOf<String>()
            if (gitService.isRepositorySynched(gitConfiguration)) {
                logger.info("[search][indexation][git-commits] project=${project.name} Git repository is synched. Indexing all commits...")
                var commitCount = 0
                gitService.forEachCommit(gitConfiguration) { commit: GitCommit ->
                    commitCount++
                    // Logging
                    if (traceCommits) {
                        logger.debug("[search][indexation][git-commits] project=${project.name} commit=${commit.shortId} message=${commit.shortMessage}")
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
        }
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