package net.nemerosa.ontrack.extension.github.ingestion.processing.job

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.git.model.GitBranchNotConfiguredException
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.github.ingestion.processing.WorkflowRunInfo
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.*
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.WorkflowJobStepConclusion
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.WorkflowJobStepStatus
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.getProjectName
import net.nemerosa.ontrack.extension.github.ingestion.settings.GitHubIngestionSettings
import net.nemerosa.ontrack.extension.github.ingestion.support.FilterHelper
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunPropertyType
import net.nemerosa.ontrack.extension.github.workflow.ValidationRunGitHubWorkflowJobProperty
import net.nemerosa.ontrack.extension.github.workflow.ValidationRunGitHubWorkflowJobPropertyType
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.*
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
        val ingestionConfig = getOrLoadIngestionConfig(build.branch)
        // Skipping job or not, depending on the configuration
        if (step == null && ingestionConfig.general.skipJobs) {
            return
        }
        // Filtering on jobs and steps
        if (ignoreJob(job, settings, ingestionConfig)) return
        if (step != null && ignoreStep(step, settings, ingestionConfig)) return
        // Gets the run property
        val runProperty = propertyService.getProperty(build, BuildGitHubWorkflowRunPropertyType::class.java).value
            ?: error("Cannot find workflow run property on build")
        // Name & description of the validation stamp
        val vsName = getValidationStampName(settings, ingestionConfig, job, step)
        val vsDescription = getValidationStampDescription(ingestionConfig, job, step)
        // Gets or creates a validation stamp for the branch
        val vs = setupValidationStamp(build.branch, vsName, vsDescription)
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

    override fun setupValidationStamp(
        branch: Branch,
        vsName: String,
        vsDescription: String?
    ): ValidationStamp {
        val existing = structureService.findValidationStampByName(branch.project.name, branch.name, vsName).getOrNull()
        return if (existing != null) {
            // Adapt description if need be
            if (vsDescription != null && vsDescription != existing.description) {
                val adapted = existing.withDescription(vsDescription)
                structureService.saveValidationStamp(
                    adapted
                )
                adapted
            } else {
                // Done
                existing
            }
        } else {
            structureService.newValidationStamp(
                ValidationStamp.of(
                    branch,
                    nd(vsName, vsDescription)
                )
            )
        }
    }

    override fun setupPromotionLevel(branch: Branch, plName: String, plDescription: String?): PromotionLevel {
        val existing = structureService.findPromotionLevelByName(branch.project.name, branch.name, plName).getOrNull()
        return if (existing != null) {
            // Adapt description if need be
            if (plDescription != null && plDescription != existing.description) {
                val adapted = existing.withDescription(plDescription)
                structureService.savePromotionLevel(
                    adapted
                )
                adapted
            } else {
                // Done
                existing
            }
        } else {
            structureService.newPromotionLevel(
                PromotionLevel.of(
                    branch,
                    nd(plName, plDescription)
                )
            )
        }
    }

    private fun ignoreJob(job: String, settings: GitHubIngestionSettings, ingestionConfig: IngestionConfig): Boolean =
        FilterHelper.excludes(job, settings.jobIncludes, settings.jobExcludes) ||
                !ingestionConfig.filterJob(job)

    private fun ignoreStep(step: String, settings: GitHubIngestionSettings, ingestionConfig: IngestionConfig): Boolean =
        FilterHelper.excludes(step, settings.stepIncludes, settings.stepExcludes) ||
                !ingestionConfig.filterStep(step)

    private fun getOrLoadIngestionConfig(branch: Branch): IngestionConfig {
        val gitBranchProperty =
            propertyService.getProperty(branch, GitBranchConfigurationPropertyType::class.java).value
        return if (gitBranchProperty != null) {
            configService.getOrLoadConfig(branch, INGESTION_CONFIG_FILE_PATH)
        } else {
            throw GitBranchNotConfiguredException(branch.id)
        }
    }

    override fun setupValidationRun(
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

    private fun getValidationStampName(settings: GitHubIngestionSettings, ingestionConfig: IngestionConfig, job: String, step: String?): String =
        ingestionConfig.getValidationStampName(settings, job, step)

    private fun getValidationStampDescription(ingestionConfig: IngestionConfig, job: String, step: String?): String =
        ingestionConfig.getValidationStampDescription(job, step)

}