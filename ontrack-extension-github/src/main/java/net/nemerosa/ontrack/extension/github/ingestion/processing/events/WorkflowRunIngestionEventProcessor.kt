package net.nemerosa.ontrack.extension.github.ingestion.processing.events

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.common.BaseException
import net.nemerosa.ontrack.extension.github.ingestion.config.model.IngestionConfig
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventPreprocessingCheck
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResultDetails
import net.nemerosa.ontrack.extension.github.ingestion.processing.WorkflowRunInfo
import net.nemerosa.ontrack.extension.github.ingestion.processing.buildid.BuildIdStrategyRegistry
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.ConfigService
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.INGESTION_CONFIG_FILE_PATH
import net.nemerosa.ontrack.extension.github.ingestion.processing.job.WorkflowJobProcessingService
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.*
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.User
import net.nemerosa.ontrack.extension.github.ingestion.support.IngestionModelAccessService
import net.nemerosa.ontrack.extension.github.ingestion.support.REFS_TAGS_PREFIX
import net.nemerosa.ontrack.extension.github.support.parseLocalDateTime
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime
import kotlin.reflect.KClass

@Component
class WorkflowRunIngestionEventProcessor(
    structureService: StructureService,
    private val runInfoService: RunInfoService,
    private val ingestionModelAccessService: IngestionModelAccessService,
    private val configService: ConfigService,
    private val workflowJobProcessingService: WorkflowJobProcessingService,
    private val buildIdStrategyRegistry: BuildIdStrategyRegistry,
) : AbstractRepositoryIngestionEventProcessor<WorkflowRunPayload>(
    structureService
) {

    override val event: String = "workflow_run"

    override val payloadType: KClass<WorkflowRunPayload> = WorkflowRunPayload::class

    /**
     * Branch or PR name, workflow name & run number.
     */
    override fun getPayloadSource(payload: WorkflowRunPayload): String? {
        val pr = payload.workflowRun.pullRequests.firstOrNull()
        val ref = if (pr != null) {
            "PR-${pr.number}"
        } else {
            payload.workflowRun.headBranch
        }
        return "${payload.workflowRun.name}#${payload.workflowRun.runNumber}@$ref"
    }

    override fun preProcessingCheck(payload: WorkflowRunPayload): IngestionEventPreprocessingCheck {
        val pr = payload.workflowRun.pullRequests.firstOrNull()
        return if (pr != null) {
            if (pr.sameRepo()) {
                IngestionEventPreprocessingCheck.TO_BE_PROCESSED
            } else {
                IngestionEventPreprocessingCheck.IGNORED
            }
        } else if (payload.workflowRun.headBranch.startsWith(REFS_TAGS_PREFIX)) {
            IngestionEventPreprocessingCheck.IGNORED
        } else {
            IngestionEventPreprocessingCheck.TO_BE_PROCESSED
        }
    }

    override fun process(payload: WorkflowRunPayload, configuration: String?): IngestionEventProcessingResultDetails {
        // Gets the ingestion configuration
        val ingestionConfig = ingestionModelAccessService.getBranchIfExists(
            repository = payload.repository,
            headBranch = payload.workflowRun.headBranch,
            pullRequest = payload.workflowRun.pullRequests.firstOrNull(),
        )
            ?.let { branch ->
                // Using the branch cache if possible
                configService.loadAndSaveConfig(branch, INGESTION_CONFIG_FILE_PATH)
            }
        // Loading the ingestion config directly from GH
            ?: ingestionModelAccessService.findGitHubEngineConfiguration(
                repository = payload.repository,
                configurationName = configuration,
            ).let { ghConfig ->
                configService.loadConfig(
                    configuration = ghConfig,
                    repository = payload.repository.fullName,
                    branch = payload.workflowRun.headBranch, // Even for a PR
                    path = INGESTION_CONFIG_FILE_PATH,
                )
            }
        // Filter on the workflow name
        return if (ingestionConfig.workflows.filter.includes(payload.workflowRun.name)) {
            // Filtering on the event
            if (!ingestionConfig.workflows.events.contains(payload.workflowRun.event)) {
                IngestionEventProcessingResultDetails.ignored(
                    """"${payload.workflowRun.event}" is not configured for ingestion."""
                )
            }
            // Filtering on the PR type
            else if (payload.workflowRun.pullRequests.isNotEmpty() && !ingestionConfig.workflows.includePRs) {
                IngestionEventProcessingResultDetails.ignored(
                    """PRs are not configured for ingestion."""
                )
            }
            // Filtering on the Git branch name
            else if (!ingestionConfig.workflows.branchFilter.includes(payload.workflowRun.headBranch)) {
                IngestionEventProcessingResultDetails.ignored(
                    """"${payload.workflowRun.headBranch}" is not configured for ingestion."""
                )
            }
            // OK to process
            else {
                when (payload.action) {
                    WorkflowRunAction.requested -> startBuild(payload, configuration, ingestionConfig)
                    WorkflowRunAction.completed -> endBuild(payload, configuration, ingestionConfig)
                }
            }
        } else {
            // Workflow is ignored
            IngestionEventProcessingResultDetails.ignored("${payload.workflowRun.name} workflow is filtered out.")
        }
    }

    private fun endBuild(
        payload: WorkflowRunPayload,
        configuration: String?,
        ingestionConfig: IngestionConfig,
    ): IngestionEventProcessingResultDetails {
        // Build creation & setup
        val build =
            getOrCreateBuild(payload, configuration = configuration, ingestionConfig = ingestionConfig)
                ?: return IngestionEventProcessingResultDetails.ignored("Build strategy does not allow the creation of a build for this workflow run.")
        // Setting the run info
        val runInfo = collectRunInfo(payload)
        runInfoService.setRunInfo(build, runInfo)
        // Run as a validation
        val config = configService.getOrLoadConfig(build.branch, INGESTION_CONFIG_FILE_PATH)
        val runName = payload.workflowRun.name
        if (config.workflows.validations.enabled && config.workflows.validations.filter.includes(runName)) {
            setupWorkflowValidation(config, build, payload.workflowRun, runInfo)
        }
        // OK
        return IngestionEventProcessingResultDetails.processed()
    }

    private fun setupWorkflowValidation(
        config: IngestionConfig,
        build: Build,
        workflowRun: WorkflowRun,
        runInfo: RunInfoInput,
    ) {
        // Gets the validation name from the run name
        val validationStampName =
            normalizeName("${config.workflows.validations.prefix}${workflowRun.name}${config.workflows.validations.suffix}")
        // Gets or creates the validation stamp
        val vs = ingestionModelAccessService.setupValidationStamp(
            build.branch, validationStampName, "${workflowRun.name} workflow"
        )
        // Creates a validation run
        val run = workflowJobProcessingService.setupValidationRun(
            build = build,
            vs = vs,
            runAttempt = 1,
            status = workflowRun.status,
            conclusion = workflowRun.conclusion,
            startedAt = workflowRun.createdAtDate,
            completedAt = workflowRun.updatedAtDate,
        )
        // Sets the run info
        if (run != null) {
            runInfoService.setRunInfo(run, runInfo)
        }
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

    private fun startBuild(
        payload: WorkflowRunPayload,
        configuration: String?,
        ingestionConfig: IngestionConfig,
    ): IngestionEventProcessingResultDetails {
        // Build creation & setup
        val build = getOrCreateBuild(payload, configuration = configuration, ingestionConfig = ingestionConfig)
        return if (build != null) {
            IngestionEventProcessingResultDetails.processed("Build ${build.name} created.")
        } else {
            IngestionEventProcessingResultDetails.ignored("Build strategy does not allow the creation of a build for this workflow run.")
        }
    }

    private fun getOrCreateBuild(
        payload: WorkflowRunPayload,
        configuration: String?,
        ingestionConfig: IngestionConfig,
    ): Build? {
        // Gets or creates the project
        val project = getOrCreateProject(payload, configuration)
        // Branch creation & setup
        val branch = getOrCreateBranch(project, payload)
        // Build identification strategy
        val strategy = buildIdStrategyRegistry.getBuildIdStrategy(ingestionConfig.workflows.buildIdStrategy.id)
        // Gets the build using this strategy
        // If not found, checks if the strategy allows for the creation of a build
        val build = strategy.findBuild(
            branch,
            payload.workflowRun,
            ingestionConfig.workflows.buildIdStrategy.config,
        )
            ?: if (strategy.canCreateBuild(
                    branch,
                    payload.workflowRun,
                    ingestionConfig.workflows.buildIdStrategy.config
                )
            ) {
                structureService.newBuild(
                    Build.of(
                        branch,
                        nd(
                            strategy.getBuildName(
                                payload.workflowRun,
                                ingestionConfig.workflows.buildIdStrategy.config
                            ),
                            "Created by GitHub workflow ${payload.workflowRun.name}"
                        ),
                        Signature.of(payload.workflowRun.createdAtDate, payload.sender?.login ?: "hook")
                    )
                )
            } else {
                return null // <- not creating a build not reusing a build
            }
        // Link between the build and the workflow
        ingestionModelAccessService.setBuildRunId(build, payload.workflowRun)
        // Build id strategy setup
        strategy.setupBuild(build, payload.workflowRun, ingestionConfig.workflows.buildIdStrategy.config)
        // OK
        return build
    }

    private fun getOrCreateBranch(project: Project, payload: WorkflowRunPayload): Branch =
        if (payload.workflowRun.pullRequests.size > 1) {
            throw WorkflowRunMoreThanOnePRException()
        } else {
            ingestionModelAccessService.getOrCreateBranch(
                project = project,
                headBranch = payload.workflowRun.headBranch,
                pullRequest = payload.workflowRun.pullRequests.firstOrNull(),
            )
        }

    private fun getOrCreateProject(payload: WorkflowRunPayload, configuration: String?): Project =
        ingestionModelAccessService.getOrCreateProject(
            repository = payload.repository,
            configuration = configuration,
        )
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
    val pullRequests: List<WorkflowRunPullRequest>,
    @JsonProperty("created_at")
    val createdAtDate: LocalDateTime,
    @JsonProperty("updated_at")
    val updatedAtDate: LocalDateTime?,
    val htmlUrl: String,
    val event: String,
    val status: WorkflowJobStepStatus,
    val conclusion: WorkflowJobStepConclusion?,
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
        pullRequests: List<WorkflowRunPullRequest>,
        @JsonProperty("created_at")
        createdAt: String,
        @JsonProperty("updated_at")
        updatedAt: String?,
        @JsonProperty("html_url")
        htmlUrl: String,
        event: String,
        status: WorkflowJobStepStatus,
        conclusion: WorkflowJobStepConclusion?,
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
        status = status,
        conclusion = conclusion,
    )

}

@JsonIgnoreProperties(ignoreUnknown = true)
data class WorkflowRunPullRequest(
    override val number: Int,
    val head: WorkflowRunPullRequestBranch,
    val base: WorkflowRunPullRequestBranch,
) : IPullRequest {
    fun sameRepo() = head.repo.url == base.repo.url
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class WorkflowRunPullRequestBranch(
    val ref: String,
    val repo: WorkflowRunPullRequestBranchRepo,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class WorkflowRunPullRequestBranchRepo(
    val name: String,
    val url: String,
)

class WorkflowRunMoreThanOnePRException : BaseException(
    "Workflow runs for more than 1 PR are not supported."
)
