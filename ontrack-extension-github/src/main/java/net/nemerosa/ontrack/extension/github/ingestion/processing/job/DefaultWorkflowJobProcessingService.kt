package net.nemerosa.ontrack.extension.github.ingestion.processing.job

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.git.model.GitBranchNotConfiguredException
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.github.ingestion.processing.WorkflowRunInfo
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.ConfigService
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.INGESTION_CONFIG_FILE_PATH
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.IngestionConfig
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.*
import net.nemerosa.ontrack.extension.github.ingestion.settings.GitHubIngestionSettings
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunPropertyType
import net.nemerosa.ontrack.extension.github.workflow.ValidationRunGitHubWorkflowJobProperty
import net.nemerosa.ontrack.extension.github.workflow.ValidationRunGitHubWorkflowJobPropertyType
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDateTime

@Service
@Transactional
class DefaultWorkflowJobProcessingService(
    private val structureService: StructureService,
    private val propertyService: PropertyService,
    private val cachedSettingsService: CachedSettingsService,
    private val runInfoService: RunInfoService,
    private val configService: ConfigService,
) : WorkflowJobProcessingService {

    override fun setupValidation(
        repository: Repository,
        runId: Long,
        runAttempt: Int,
        job: String,
        jobUrl: String,
        step: String?,
        status: WorkflowJobStepStatus,
        conclusion: WorkflowJobStepConclusion?,
        startedAt: LocalDateTime?,
        completedAt: LocalDateTime?,
    ) {
        // Settings
        val settings = cachedSettingsService.getCachedSettings(GitHubIngestionSettings::class.java)
        // Project name
        val projectName = getProjectName(repository.owner.login, repository.name, settings.orgProjectPrefix)
        // Gets the build
        val build = findBuild(projectName, runId)
            ?: throw BuildWithWorkflowRunIdNotFoundException(projectName, runId)
        // Gets the ingestion configuration
        val ingestionConfig = getOrLoadIngestionConfig(repository, build.branch)
        // Gets the run property
        val runProperty = propertyService.getProperty(build, BuildGitHubWorkflowRunPropertyType::class.java).value
            ?: error("Cannot find workflow run property on build")
        // Name & description of the validation stamp
        val vsName = getValidationStampName(ingestionConfig, job, step)
        val vsDescription = getValidationStampDescription(ingestionConfig, job, step)
        // Gets or creates a validation stamp for the branch
        val vs = structureService.findValidationStampByName(build.project.name, build.branch.name, vsName).getOrNull()
            ?: structureService.newValidationStamp(
                ValidationStamp.of(
                    build.branch,
                    nd(vsName, vsDescription)
                )
            )
        // Gets or creates the validation run based on the job number
        val run = setupValidationRun(
            build = build,
            vs = vs,
            runAttempt = runAttempt,
            status = status,
            conclusion = conclusion,
            startedAt = startedAt,
            completedAt = completedAt,
        )
        // Sets the run info if step is finished
        if (run != null && startedAt != null && completedAt != null) {
            val seconds = Duration.between(startedAt, completedAt).toSeconds().toInt()
            val info = RunInfoInput(
                sourceType = WorkflowRunInfo.TYPE,
                sourceUri = jobUrl,
                runTime = seconds,
            )
            runInfoService.setRunInfo(run, info)
        }
        // Property
        if (run != null && !propertyService.hasProperty(run, ValidationRunGitHubWorkflowJobPropertyType::class.java)) {
            propertyService.editProperty(
                run,
                ValidationRunGitHubWorkflowJobPropertyType::class.java,
                ValidationRunGitHubWorkflowJobProperty(
                    runId = runId,
                    url = jobUrl,
                    name = runProperty.name,
                    runNumber = runProperty.runNumber,
                    job = job,
                    running = false, // Run won't be created until finished
                )
            )
        }
    }

    private fun getOrLoadIngestionConfig(repository: Repository, branch: Branch): IngestionConfig {
        val gitBranchProperty =
            propertyService.getProperty(branch, GitBranchConfigurationPropertyType::class.java).value
        return if (gitBranchProperty != null) {
            val gitBranch = gitBranchProperty.branch
            configService.getOrLoadConfig(repository, gitBranch, INGESTION_CONFIG_FILE_PATH)
        } else {
            throw GitBranchNotConfiguredException(branch.id)
        }
    }

    private fun setupValidationRun(
        build: Build,
        vs: ValidationStamp,
        runAttempt: Int,
        status: WorkflowJobStepStatus,
        conclusion: WorkflowJobStepConclusion?,
        startedAt: LocalDateTime?,
        completedAt: LocalDateTime?
    ): ValidationRun? {
        // If not finished, does not do anything
        if (conclusion == null || startedAt == null || completedAt == null) return null
        // If skipped, does not do anything
        if (conclusion == WorkflowJobStepConclusion.skipped) return null
        // Gets the status
        val runStatus = getValidationRunStatus(status, conclusion) ?: return null
        // Gets the existing validation runs
        val runsCount = structureService.getValidationRunsCountForBuildAndValidationStamp(
            build.id,
            vs.id,
        )
        // If there are less runs than the mentioned number of attempts, we need to create a new run
        return if (runsCount < runAttempt) {
            structureService.newValidationRun(
                build,
                ValidationRunRequest(
                    validationStampName = vs.name,
                    validationRunStatusId = runStatus,
                )
            )
        } else {
            null
        }
    }

    private fun getValidationRunStatus(
        status: WorkflowJobStepStatus,
        conclusion: WorkflowJobStepConclusion
    ): ValidationRunStatusID? =
        when (status) {
            WorkflowJobStepStatus.completed -> when (conclusion) {
                WorkflowJobStepConclusion.success -> ValidationRunStatusID.STATUS_PASSED
                WorkflowJobStepConclusion.failure -> ValidationRunStatusID.STATUS_FAILED
                else -> null
            }
            else -> null
        }

    fun findBuild(projectName: String, runId: Long): Build? {
        val project = structureService.findProjectByName(projectName).getOrNull()
            ?: return null
        return propertyService.findByEntityTypeAndSearchArguments(
            ProjectEntityType.BUILD,
            BuildGitHubWorkflowRunPropertyType::class,
            PropertySearchArguments(
                jsonContext = null,
                jsonCriteria = "(pp.json->>'runId')::int = :runId",
                criteriaParams = mapOf(
                    "runId" to runId,
                )
            )
        ).map {
            structureService.getBuild(it)
        }.firstOrNull {
            it.project.id == project.id
        }
    }

    private fun getValidationStampName(ingestionConfig: IngestionConfig, job: String, step: String?): String {
        val baseName = if (step != null) {
            "$job-$step"
        } else {
            job
        }
        // TODO Mapping in the ingestion configuration
        return normalizeName(baseName)
    }

    private fun getValidationStampDescription(ingestionConfig: IngestionConfig, job: String, step: String?): String =
        // TODO Mapping in the ingestion configuration
        if (step != null) {
            "$job $step"
        } else {
            job
        }

}