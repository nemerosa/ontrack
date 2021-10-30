package net.nemerosa.ontrack.extension.github.ingestion.processing.job

import net.nemerosa.ontrack.extension.github.ingestion.processing.model.WorkflowJobStepConclusion
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.WorkflowJobStepStatus
import java.time.LocalDateTime

interface WorkflowJobProcessingService {
    /**
     * Creates or updates a validation run.
     *
     * @param owner Repository owner
     * @param repository Repository name
     * @param runId Workflow run ID. Used to identify the build.
     * @param job Name of the workflow job
     * @param step Name of the step in the job (null when considering the job only
     * @param status Current status of the validation
     * @param conclusion Conclusion of the validation
     * @param startedAt Start timestamp for the validation
     * @param completedAt Completion timestamp for the validation
     */
    fun setupValidation(
        owner: String,
        repository: String,
        runId: Long,
        job: String,
        step: String?,
        status: WorkflowJobStepStatus,
        conclusion: WorkflowJobStepConclusion?,
        startedAt: LocalDateTime?,
        completedAt: LocalDateTime?
    )
}