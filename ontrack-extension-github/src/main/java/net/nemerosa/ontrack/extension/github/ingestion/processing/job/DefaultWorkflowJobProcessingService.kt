package net.nemerosa.ontrack.extension.github.ingestion.processing.job

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.WorkflowJobStepConclusion
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.WorkflowJobStepStatus
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.getProjectName
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.normalizeName
import net.nemerosa.ontrack.extension.github.ingestion.settings.GitHubIngestionSettings
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunPropertyType
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class DefaultWorkflowJobProcessingService(
    private val structureService: StructureService,
    private val propertyService: PropertyService,
    private val cachedSettingsService: CachedSettingsService,
) : WorkflowJobProcessingService {

    override fun setupValidation(
        owner: String,
        repository: String,
        runId: Long,
        runAttempt: Int,
        job: String,
        step: String?,
        status: WorkflowJobStepStatus,
        conclusion: WorkflowJobStepConclusion?,
        startedAt: LocalDateTime?,
        completedAt: LocalDateTime?,
    ) {
        // Settings
        val settings = cachedSettingsService.getCachedSettings(GitHubIngestionSettings::class.java)
        // Project name
        val projectName = getProjectName(owner, repository, settings.orgProjectPrefix)
        // Gets the build
        val build = findBuild(projectName, runId)
            ?: throw BuildWithWorkflowRunIdNotFoundException(projectName, runId)
        // Name & description of the validation stamp
        val vsName = getValidationStampName(job, step)
        val vsDescription = getValidationStampDescription(job, step)
        // Gets or creates a validation stamp for the branch
        val vs = structureService.newValidationStamp(
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
        // TODO Sets the run info if step is finished
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
                // TODO Error status
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

    private fun getValidationStampName(job: String, step: String?): String {
        val baseName = if (step != null) {
            "$job-$step"
        } else {
            job
        }
        // TODO Mapping in the ingestion configuration
        return normalizeName(baseName)
    }

    private fun getValidationStampDescription(job: String, step: String?): String =
        // TODO Mapping in the ingestion configuration
        if (step != null) {
            "$job $step"
        } else {
            job
        }

}