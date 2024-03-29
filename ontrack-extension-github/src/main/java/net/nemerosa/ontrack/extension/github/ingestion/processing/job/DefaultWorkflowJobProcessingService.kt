package net.nemerosa.ontrack.extension.github.ingestion.processing.job

import net.nemerosa.ontrack.extension.git.model.GitBranchNotConfiguredException
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.github.ingestion.config.model.IngestionConfig
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResultDetails
import net.nemerosa.ontrack.extension.github.ingestion.processing.WorkflowRunInfo
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.*
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.WorkflowJobStepConclusion
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.WorkflowJobStepStatus
import net.nemerosa.ontrack.extension.github.ingestion.support.IngestionModelAccessService
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunPropertyType
import net.nemerosa.ontrack.extension.github.workflow.ValidationRunGitHubWorkflowJobProperty
import net.nemerosa.ontrack.extension.github.workflow.ValidationRunGitHubWorkflowJobPropertyType
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDateTime

@Service
@Transactional
class DefaultWorkflowJobProcessingService(
    private val structureService: StructureService,
    private val propertyService: PropertyService,
    private val runInfoService: RunInfoService,
    private val configService: ConfigService,
    private val ingestionModelAccessService: IngestionModelAccessService,
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
    ): IngestionEventProcessingResultDetails {
        // NOTE: There is no need to filter on the workflows because if the workflow is not
        // run, its run ID is not stored in the build and no build will be found in the
        // following `findBuildByRunId` call.
        // Gets the build or does not do anything
        val build = ingestionModelAccessService.findBuildByRunId(repository, runId)
            ?: return IngestionEventProcessingResultDetails.ignored("No build with workflow run ID $runId.")
        // Gets the ingestion configuration
        val ingestionConfig = getOrLoadIngestionConfig(build.branch)
        // Filtering on jobs and steps
        if (ignoreJob(job, ingestionConfig)) {
            return IngestionEventProcessingResultDetails.ignored(
                """$job job not processed as a step because filtered out by the configuration."""
            )
        }
        if (step != null && ignoreStep(step, ingestionConfig)) {
            return IngestionEventProcessingResultDetails.ignored(
                """$step step not processed because filtered out by the configuration."""
            )
        }
        // Gets the run property
        val runProperty = propertyService.getPropertyValue(build, BuildGitHubWorkflowRunPropertyType::class.java)
            ?.findRun(runId)
            ?: error("Cannot find workflow run property on build")
        // Name & description of the validation stamp
        val vsName = ingestionConfig.getValidationStampName(job, step)
        val vsDescription = ingestionConfig.getValidationStampDescription(job, step)
        // Gets or creates a validation stamp for the branch
        val vs = ingestionModelAccessService.setupValidationStamp(build.branch, vsName, vsDescription)
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
                    event = runProperty.event,
                )
            )
        }
        // OK
        return if (run != null) {
            IngestionEventProcessingResultDetails.processed("Validation run created.")
        } else {
            IngestionEventProcessingResultDetails.ignored("Validation run not created.")
        }
    }

    private fun ignoreJob(job: String, ingestionConfig: IngestionConfig): Boolean =
        !ingestionConfig.jobs.filter.includes(job)

    private fun ignoreStep(step: String, ingestionConfig: IngestionConfig): Boolean =
        !ingestionConfig.steps.filter.includes(step)

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
        completedAt: LocalDateTime?,
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
        conclusion: WorkflowJobStepConclusion,
    ): ValidationRunStatusID? =
        when (status) {
            WorkflowJobStepStatus.completed -> when (conclusion) {
                WorkflowJobStepConclusion.success -> ValidationRunStatusID.STATUS_PASSED
                WorkflowJobStepConclusion.failure -> ValidationRunStatusID.STATUS_FAILED
                else -> null
            }
            else -> null
        }

}