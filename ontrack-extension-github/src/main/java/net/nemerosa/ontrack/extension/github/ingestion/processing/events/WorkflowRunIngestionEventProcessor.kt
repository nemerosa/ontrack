package net.nemerosa.ontrack.extension.github.ingestion.processing.events

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.git.model.ConfiguredBuildGitCommitLink
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.property.GitCommitProperty
import net.nemerosa.ontrack.extension.git.property.GitCommitPropertyType
import net.nemerosa.ontrack.extension.git.support.GitCommitPropertyCommitLink
import net.nemerosa.ontrack.extension.github.ingestion.processing.*
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.*
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.User
import net.nemerosa.ontrack.extension.github.ingestion.settings.GitHubIngestionSettings
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationProperty
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService
import net.nemerosa.ontrack.extension.github.support.parseLocalDateTime
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunProperty
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunPropertyType
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import net.nemerosa.ontrack.model.support.NoConfig
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime
import kotlin.reflect.KClass

@Component
class WorkflowRunIngestionEventProcessor(
    structureService: StructureService,
    private val gitHubConfigurationService: GitHubConfigurationService,
    private val propertyService: PropertyService,
    private val gitCommitPropertyCommitLink: GitCommitPropertyCommitLink,
    private val runInfoService: RunInfoService,
    private val cachedSettingsService: CachedSettingsService,
) : AbstractRepositoryIngestionEventProcessor<WorkflowRunPayload>(
    structureService
) {

    override val event: String = "workflow_run"

    override val payloadType: KClass<WorkflowRunPayload> = WorkflowRunPayload::class

    override fun process(payload: WorkflowRunPayload): IngestionEventProcessingResult =
        when (payload.action) {
            WorkflowRunAction.requested -> startBuild(payload)
            WorkflowRunAction.completed -> endBuild(payload)
        }

    private fun endBuild(payload: WorkflowRunPayload): IngestionEventProcessingResult {
        // Build creation & setup
        val build = getOrCreateBuild(payload, running = false)
        // Setting the run info
        val runInfo = collectRunInfo(payload)
        runInfoService.setRunInfo(build, runInfo)
        // OK
        return IngestionEventProcessingResult.PROCESSED
    }

    private fun collectRunInfo(payload: WorkflowRunPayload): RunInfoInput {
        // Build duration
        val duration = if (payload.workflowRun.updatedAtDate != null) {
            Duration.between(payload.workflowRun.createdAtDate, payload.workflowRun.updatedAtDate)
        } else {
            null
        }
        // Run info
        return RunInfoInput(
            sourceType = WorkflowRunInfo.TYPE,
            sourceUri = payload.workflowRun.htmlUrl,
            runTime = duration?.toSeconds()?.toInt(),
            triggerType = payload.workflowRun.event,
        )
    }

    private fun startBuild(payload: WorkflowRunPayload): IngestionEventProcessingResult {
        // Build creation & setup
        getOrCreateBuild(payload, running = true)
        // OK
        return IngestionEventProcessingResult.PROCESSED
    }

    private fun getOrCreateBuild(payload: WorkflowRunPayload, running: Boolean): Build {
        // Gets or creates the project
        val project = getOrCreateProject(payload)
        // Branch creation & setup
        val branch = getOrCreateBranch(project, payload)
        // Build creation & setup
        val buildName = normalizeName(
            "${payload.workflowRun.name}-${payload.workflowRun.runNumber}"
        )
        val build = structureService.findBuildByName(project.name, branch.name, buildName)
            .getOrNull()
            ?: structureService.newBuild(
                Build.of(
                    branch,
                    nd(buildName, ""),
                    Signature.of(payload.workflowRun.createdAtDate, payload.sender?.login ?: "hook")
                )
            )
        // Link between the build and the workflow
        propertyService.editProperty(
            build,
            BuildGitHubWorkflowRunPropertyType::class.java,
            BuildGitHubWorkflowRunProperty(
                runId = payload.workflowRun.id,
                url = payload.workflowRun.htmlUrl,
                name = payload.workflowRun.name,
                runNumber = payload.workflowRun.runNumber,
                running = running,
            )
        )
        // Git commit property
        if (!propertyService.hasProperty(build, GitCommitPropertyType::class.java)) {
            propertyService.editProperty(
                build,
                GitCommitPropertyType::class.java,
                GitCommitProperty(payload.workflowRun.headSha)
            )
        }
        // OK
        return build
    }

    private fun getOrCreateBranch(project: Project, payload: WorkflowRunPayload): Branch {
        if (payload.workflowRun.isPullRequest()) {
            TODO("Pull requests are not supported yet.")
        } else {
            val gitBranch = payload.workflowRun.headBranch
            val branchName = normalizeName(gitBranch)
            val branch = structureService.findBranchByName(project.name, branchName)
                .getOrNull()
                ?: structureService.newBranch(
                    Branch.of(
                        project,
                        nd(
                            name = branchName,
                            description = "$gitBranch branch",
                        )
                    )
                )
            // Setup the Git configuration for this branch
            if (!propertyService.hasProperty(branch, GitBranchConfigurationPropertyType::class.java)) {
                propertyService.editProperty(
                    branch,
                    GitBranchConfigurationPropertyType::class.java,
                    GitBranchConfigurationProperty(
                        branch = gitBranch,
                        buildCommitLink = ConfiguredBuildGitCommitLink(
                            gitCommitPropertyCommitLink,
                            NoConfig.INSTANCE
                        ).toServiceConfiguration(),
                        isOverride = false,
                        buildTagInterval = 0,
                    )
                )
            }
            // OK
            return branch
        }
    }

    private fun getOrCreateProject(payload: WorkflowRunPayload): Project {
        val settings = cachedSettingsService.getCachedSettings(GitHubIngestionSettings::class.java)
        val name = getProjectName(
            owner = payload.repository.owner.login,
            repository = payload.repository.name,
            orgProjectPrefix = settings.orgProjectPrefix,
        )
        val project = structureService.findProjectByName(name)
            .getOrNull()
            ?: structureService.newProject(
                Project.of(
                    nd(
                        name = name,
                        description = payload.repository.description,
                    )
                )
            )
        // Setup the Git configuration for this project
        setupProjectGitHubConfiguration(project, payload)
        // OK
        return project
    }

    private fun setupProjectGitHubConfiguration(project: Project, payload: WorkflowRunPayload) {
        if (!propertyService.hasProperty(project, GitHubProjectConfigurationPropertyType::class.java)) {
            // Gets the list of GH configs
            val configurations = gitHubConfigurationService.configurations
            // If no configuration, error
            val configuration = if (configurations.isEmpty()) {
                throw NoGitHubConfigException()
            }
            // If only 1 config, use it
            else if (configurations.size == 1) {
                val candidate = configurations.first()
                // Checks the URL
                if (payload.workflowRun.htmlUrl.startsWith(candidate.url)) {
                    candidate
                } else {
                    throw GitHubConfigURLMismatchException(payload.workflowRun.htmlUrl)
                }
            }
            // If several configurations, select it based on the URL
            else {
                val candidates = configurations.filter {
                    payload.workflowRun.htmlUrl.startsWith(it.url)
                }
                if (candidates.isEmpty()) {
                    throw GitHubConfigURLNoMatchException(payload.workflowRun.htmlUrl)
                } else if (candidates.size == 1) {
                    candidates.first()
                } else {
                    throw GitHubConfigURLSeveralMatchesException(payload.workflowRun.htmlUrl)
                }
            }
            // Project property if not already defined
            propertyService.editProperty(
                project,
                GitHubProjectConfigurationPropertyType::class.java,
                GitHubProjectConfigurationProperty(
                    configuration = configuration,
                    repository = payload.repository.fullName,
                    indexationInterval = 30, // TODO Make it configurable
                    issueServiceConfigurationIdentifier = "self"  // TODO Make it configurable
                )
            )
        }
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class WorkflowRunPayload(
    val action: WorkflowRunAction,
    @JsonProperty("workflow_run")
    val workflowRun: WorkflowRun,
    repository: Repository,
    val sender: User?,
) : AbstractRepositoryPayload(
    repository,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WorkflowRunPayload) return false

        if (action != other.action) return false
        if (workflowRun != other.workflowRun) return false
        if (sender != other.sender) return false

        return true
    }

    override fun hashCode(): Int {
        var result = action.hashCode()
        result = 31 * result + workflowRun.hashCode()
        result = 31 * result + (sender?.hashCode() ?: 0)
        return result
    }
}

@Suppress("EnumEntryName")
enum class WorkflowRunAction {
    requested,
    completed
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class WorkflowRun internal constructor(
    val id: Long,
    val name: String,
    val runNumber: Int,
    val headBranch: String,
    val headSha: String,
    val pullRequests: List<PullRequest>,
    val createdAtDate: LocalDateTime,
    val updatedAtDate: LocalDateTime?,
    val htmlUrl: String,
    val event: String,
) {

    @JsonCreator
    constructor(
        id: Long,
        name: String,
        @JsonProperty("run_number")
        runNumber: Int,
        @JsonProperty("head_branch")
        headBranch: String,
        @JsonProperty("head_sha")
        headSha: String,
        @JsonProperty("pull_requests")
        pullRequests: List<PullRequest>,
        @JsonProperty("created_at")
        createdAt: String,
        @JsonProperty("updated_at")
        updatedAt: String?,
        @JsonProperty("html_url")
        htmlUrl: String,
        event: String,
    ) : this(
        id = id,
        name = name,
        runNumber = runNumber,
        headBranch = headBranch,
        headSha = headSha,
        pullRequests = pullRequests,
        createdAtDate = parseLocalDateTime(createdAt),
        updatedAtDate = updatedAt?.run { parseLocalDateTime(this) },
        htmlUrl = htmlUrl,
        event = event,
    )

    fun isPullRequest() = pullRequests.isNotEmpty()

}