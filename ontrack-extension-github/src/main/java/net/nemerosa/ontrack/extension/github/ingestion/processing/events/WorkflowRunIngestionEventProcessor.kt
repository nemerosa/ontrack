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
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventPreprocessingCheck
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResult
import net.nemerosa.ontrack.extension.github.ingestion.processing.WorkflowRunInfo
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.PullRequest
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.User
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.normalizeName
import net.nemerosa.ontrack.extension.github.ingestion.support.IngestionModelAccessService
import net.nemerosa.ontrack.extension.github.support.parseLocalDateTime
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunProperty
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunPropertyType
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import net.nemerosa.ontrack.model.support.NoConfig
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime
import kotlin.reflect.KClass

@Component
class WorkflowRunIngestionEventProcessor(
    structureService: StructureService,
    private val propertyService: PropertyService,
    private val gitCommitPropertyCommitLink: GitCommitPropertyCommitLink,
    private val runInfoService: RunInfoService,
    private val ingestionModelAccessService: IngestionModelAccessService,
) : AbstractRepositoryIngestionEventProcessor<WorkflowRunPayload>(
    structureService
) {

    override val event: String = "workflow_run"

    override val payloadType: KClass<WorkflowRunPayload> = WorkflowRunPayload::class

    override fun preProcessingCheck(payload: WorkflowRunPayload): IngestionEventPreprocessingCheck {
        return IngestionEventPreprocessingCheck.TO_BE_PROCESSED
    }

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
        val branch = ingestionModelAccessService.getOrCreateBranch(
            project = project,
            headBranch = payload.workflowRun.headBranch,
            baseBranch = null, // TODO Missing PR support
        )
        // Setup the Git configuration for this branch
        if (!propertyService.hasProperty(branch, GitBranchConfigurationPropertyType::class.java)) {
            propertyService.editProperty(
                branch,
                GitBranchConfigurationPropertyType::class.java,
                GitBranchConfigurationProperty(
                    branch = payload.workflowRun.headBranch,
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

    private fun getOrCreateProject(payload: WorkflowRunPayload): Project =
        ingestionModelAccessService.getOrCreateProject(
            repository = payload.repository,
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
    val pullRequests: List<PullRequest>,
    @JsonProperty("created_at")
    val createdAtDate: LocalDateTime,
    @JsonProperty("updated_at")
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

}