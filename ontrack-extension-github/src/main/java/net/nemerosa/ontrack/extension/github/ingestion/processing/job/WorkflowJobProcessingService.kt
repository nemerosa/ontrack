package net.nemerosa.ontrack.extension.github.ingestion.processing.job

import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.WorkflowJobStepConclusion
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.WorkflowJobStepStatus
import net.nemerosa.ontrack.model.structure.*
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
     * @return If items have been created
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
        completedAt: LocalDateTime?
    ): Boolean

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
        completedAt: LocalDateTime?
    ): ValidationRun?

    /**
     * Gets or creates a validation stamp
     *
     * @param branch Parent branch
     * @param vsName Name of the validation stamp
     * @param vsDescription Description of the validation stamp
     */
    fun setupValidationStamp(
        branch: Branch,
        vsName: String,
        vsDescription: String?
    ): ValidationStamp

    /**
     * Gets or creates a promotion level
     *
     * @param branch Parent branch
     * @param plName Name of the promotion level
     * @param plDescription Description of the promotion level
     */
    fun setupPromotionLevel(
        branch: Branch,
        plName: String,
        plDescription: String?
    ): PromotionLevel
}