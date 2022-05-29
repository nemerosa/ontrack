package net.nemerosa.ontrack.extension.github.ingestion.processing.job

import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResultDetails
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.WorkflowJobStepConclusion
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.WorkflowJobStepStatus
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ValidationRun
import net.nemerosa.ontrack.model.structure.ValidationStamp
import java.time.LocalDateTime

interface WorkflowJobProcessingService {
    /**
     * Creates or updates a validation run.
     *
     * @param repository Repository
     * @param runId Workflow run ID. Used to identify the build.
     * @param runAttempt Number of times this job was attempted (starts with 1, with the very first run)
     * @param job Name of the workflow job
     * @param jobUrl URL to the job
     * @param step Name of the step in the job (null when considering the job only
     * @param status Current status of the validation
     * @param conclusion Conclusion of the validation
     * @param startedAt Start timestamp for the validation
     * @param completedAt Completion timestamp for the validation
     * @return Result of the processing
     */
    fun setupValidation(
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
    ): IngestionEventProcessingResultDetails

    /**
     * Generic method to create a validation run
     */
    fun setupValidationRun(
        build: Build,
        vs: ValidationStamp,
        runAttempt: Int,
        status: WorkflowJobStepStatus,
        conclusion: WorkflowJobStepConclusion?,
        startedAt: LocalDateTime?,
        completedAt: LocalDateTime?,
    ): ValidationRun?
}