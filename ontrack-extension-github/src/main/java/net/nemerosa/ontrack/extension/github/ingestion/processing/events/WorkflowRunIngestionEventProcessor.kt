package net.nemerosa.ontrack.extension.github.ingestion.processing.events

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.common.BaseException
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.general.AutoPromotionProperty
import net.nemerosa.ontrack.extension.general.AutoPromotionPropertyType
import net.nemerosa.ontrack.extension.git.property.GitCommitProperty
import net.nemerosa.ontrack.extension.git.property.GitCommitPropertyType
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventPreprocessingCheck
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResult
import net.nemerosa.ontrack.extension.github.ingestion.processing.WorkflowRunInfo
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.ConfigService
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.INGESTION_CONFIG_FILE_PATH
import net.nemerosa.ontrack.extension.github.ingestion.processing.job.WorkflowJobProcessingService
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.*
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.User
import net.nemerosa.ontrack.extension.github.ingestion.settings.GitHubIngestionSettings
import net.nemerosa.ontrack.extension.github.ingestion.support.FilterHelper
import net.nemerosa.ontrack.extension.github.ingestion.support.IngestionModelAccessService
import net.nemerosa.ontrack.extension.github.support.parseLocalDateTime
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunProperty
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunPropertyType
import net.nemerosa.ontrack.model.settings.CachedSettingsService
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
    private val propertyService: PropertyService,
    private val runInfoService: RunInfoService,
    private val ingestionModelAccessService: IngestionModelAccessService,
    private val configService: ConfigService,
    private val workflowJobProcessingService: WorkflowJobProcessingService,
    private val cachedSettingsService: CachedSettingsService,
) : AbstractRepositoryIngestionEventProcessor<WorkflowRunPayload>(
    structureService
) {

    override val event: String = "workflow_run"

    override val payloadType: KClass<WorkflowRunPayload> = WorkflowRunPayload::class

    override fun preProcessingCheck(payload: WorkflowRunPayload): IngestionEventPreprocessingCheck {
        return IngestionEventPreprocessingCheck.TO_BE_PROCESSED
    }

    override fun process(payload: WorkflowRunPayload, configuration: String?): IngestionEventProcessingResult =
        when (payload.action) {
            WorkflowRunAction.requested -> startBuild(payload, configuration)
            WorkflowRunAction.completed -> endBuild(payload, configuration)
        }

    private fun endBuild(payload: WorkflowRunPayload, configuration: String?): IngestionEventProcessingResult {
        // Build creation & setup
        val build = getOrCreateBuild(payload, running = false, configuration = configuration)
        // Setting the run info
        val runInfo = collectRunInfo(payload)
        runInfoService.setRunInfo(build, runInfo)
        // Run as a validation
        val settings = cachedSettingsService.getCachedSettings(GitHubIngestionSettings::class.java)
        val config = configService.getOrLoadConfig(build.branch, INGESTION_CONFIG_FILE_PATH)
        val runValidationEnabled = config.runs.enabled ?: settings.runValidations
        if (runValidationEnabled) {
            val runName = payload.workflowRun.name
            if (FilterHelper.includes(runName, config.runs.filter.includes, config.runs.filter.excludes)) {
                setupRunValidation(build, payload.workflowRun, runInfo)
            }
        }
        // OK
        return IngestionEventProcessingResult.PROCESSED
    }

    private fun setupRunValidation(build: Build, workflowRun: WorkflowRun, runInfo: RunInfoInput) {
        // Gets the validation name from the run name
        val validationStampName = normalizeName(workflowRun.name) + "-run"
        // Gets or creates the validation stamp
        val vs = workflowJobProcessingService.setupValidationStamp(
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

    private fun startBuild(payload: WorkflowRunPayload, configuration: String?): IngestionEventProcessingResult {
        // Build creation & setup
        val build = getOrCreateBuild(payload, running = true, configuration = configuration)
        // Auto promotion configuration
        autoPromotionConfiguration(build)
        // OK
        return IngestionEventProcessingResult.PROCESSED
    }

    private fun autoPromotionConfiguration(build: Build) {
        // Gets or loads the ingestion configuration
        val config = configService.getOrLoadConfig(build.branch, INGESTION_CONFIG_FILE_PATH)
        // Making sure all validations are created
        val validations = config.promotions.flatMap { it.validations }.distinct().associateWith { validation ->
            workflowJobProcessingService.setupValidationStamp(build.branch, validation, null)
        }
        // Creating all promotions - first pass
        val promotions = config.promotions.associate { plConfig ->
            plConfig.name to workflowJobProcessingService.setupPromotionLevel(
                build.branch,
                plConfig.name,
                plConfig.description
            )
        }
        // Configuring all promotions - second pass
        config.promotions.forEach { plConfig ->
            val promotion = promotions[plConfig.name]
            if (promotion != null) {
                val existingAutoPromotionProperty: AutoPromotionProperty? =
                    propertyService.getProperty(promotion, AutoPromotionPropertyType::class.java).value
                val autoPromotionProperty = AutoPromotionProperty(
                    validationStamps = plConfig.validations.mapNotNull { validations[it] },
                    promotionLevels = plConfig.promotions.mapNotNull { promotions[it] },
                    include = plConfig.include ?: "",
                    exclude = plConfig.exclude ?: "",
                )
                if (existingAutoPromotionProperty == null || existingAutoPromotionProperty != autoPromotionProperty) {
                    propertyService.editProperty(promotion,
                        AutoPromotionPropertyType::class.java,
                        autoPromotionProperty)
                }
            }
        }
    }

    private fun getOrCreateBuild(payload: WorkflowRunPayload, running: Boolean, configuration: String?): Build {
        // Gets or creates the project
        val project = getOrCreateProject(payload, configuration)
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
    val pullRequests: List<PullRequest>,
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
        pullRequests: List<PullRequest>,
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

class WorkflowRunMoreThanOnePRException : BaseException(
    "Workflow runs for more than 1 PR are not supported."
)
