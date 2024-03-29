package net.nemerosa.ontrack.extension.github.ingestion.processing.events

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventPreprocessingCheck
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResultDetails
import net.nemerosa.ontrack.extension.github.ingestion.processing.job.WorkflowJobProcessingService
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.WorkflowJobStepConclusion
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.WorkflowJobStepStatus
import net.nemerosa.ontrack.extension.github.support.parseLocalDateTime
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import kotlin.reflect.KClass

@Component
class WorkflowJobIngestionEventProcessor(
    structureService: StructureService,
    private val workflowJobProcessingService: WorkflowJobProcessingService,
) : AbstractRepositoryIngestionEventProcessor<WorkflowJobPayload>(
    structureService
) {

    override val event: String = "workflow_job"

    override val payloadType: KClass<WorkflowJobPayload> = WorkflowJobPayload::class

    /**
     * Run ID as the source
     */
    override fun getPayloadSource(payload: WorkflowJobPayload): String? =
        payload.workflowJob.runId.toString()

    override fun preProcessingCheck(payload: WorkflowJobPayload): IngestionEventPreprocessingCheck {
        return IngestionEventPreprocessingCheck.TO_BE_PROCESSED
    }

    override fun process(payload: WorkflowJobPayload, configuration: String?): IngestionEventProcessingResultDetails =
        when (payload.action) {
            WorkflowJobAction.in_progress -> onJob(payload)
            WorkflowJobAction.completed -> onJob(payload)
            else -> IngestionEventProcessingResultDetails.ignored("Workflow job action ${payload.action} is not processed.")
        }

    private fun onJob(payload: WorkflowJobPayload): IngestionEventProcessingResultDetails {
        // Processes each step independently
        payload.workflowJob.steps?.forEach { step ->
            onStep(step, payload)
        }
        // Processing of the job itself
        return workflowJobProcessingService.setupValidation(
            repository = payload.repository,
            runId = payload.workflowJob.runId,
            runAttempt = payload.workflowJob.runAttempt,
            job = payload.workflowJob.name,
            jobUrl = payload.workflowJob.htmlUrl,
            step = null,
            status = payload.workflowJob.status,
            conclusion = payload.workflowJob.conclusion,
            startedAt = payload.workflowJob.startedAtDate,
            completedAt = payload.workflowJob.completedAtDate,
        )
    }

    private fun onStep(step: WorkflowJobStep, payload: WorkflowJobPayload) =
        workflowJobProcessingService.setupValidation(
            repository = payload.repository,
            runId = payload.workflowJob.runId,
            runAttempt = payload.workflowJob.runAttempt,
            job = payload.workflowJob.name,
            jobUrl = payload.workflowJob.htmlUrl,
            step = step.name,
            status = step.status,
            conclusion = step.conclusion,
            startedAt = step.startedAtDate,
            completedAt = step.completedAtDate,
        )
}

@JsonIgnoreProperties(ignoreUnknown = true)
class WorkflowJobPayload(
    val action: WorkflowJobAction,
    @JsonProperty("workflow_job")
    val workflowJob: WorkflowJob,
    repository: Repository,
) : AbstractRepositoryPayload(
    repository,
)

@Suppress("EnumEntryName")
enum class WorkflowJobAction {
    queued,
    in_progress,
    completed,
}

@JsonIgnoreProperties(ignoreUnknown = true)
class WorkflowJob(
    val runId: Long,
    val runAttempt: Int,
    val status: WorkflowJobStepStatus,
    val conclusion: WorkflowJobStepConclusion?,
    val startedAtDate: LocalDateTime?,
    val completedAtDate: LocalDateTime?,
    val name: String,
    val steps: List<WorkflowJobStep>?,
    val htmlUrl: String,
) {
    @JsonCreator
    constructor(
        @JsonProperty("run_id")
        runId: Long,
        @JsonProperty("run_attempt")
        runAttempt: Int,
        status: WorkflowJobStepStatus,
        conclusion: WorkflowJobStepConclusion?,
        @JsonProperty("started_at")
        startedAt: String?,
        @JsonProperty("completed_at")
        completedAt: String?,
        name: String,
        steps: List<WorkflowJobStep>?,
        @JsonProperty("html_url")
        htmlUrl: String,
    ) : this(
        runId = runId,
        runAttempt = runAttempt,
        status = status,
        conclusion = conclusion,
        startedAtDate = startedAt?.let { parseLocalDateTime(it) },
        completedAtDate = completedAt?.let { parseLocalDateTime(it) },
        name = name,
        steps = steps,
        htmlUrl = htmlUrl,
    )
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class WorkflowJobStep(
    val name: String,
    val status: WorkflowJobStepStatus,
    val conclusion: WorkflowJobStepConclusion?,
    val number: Int,
    val startedAtDate: LocalDateTime?,
    val completedAtDate: LocalDateTime?,
) {
    @JsonCreator
    constructor(
        name: String,
        status: WorkflowJobStepStatus,
        conclusion: WorkflowJobStepConclusion?,
        number: Int,
        @JsonProperty("started_at")
        startedAt: String?,
        @JsonProperty("completed_at")
        completedAt: String?,
    ) : this(
        name = name,
        status = status,
        conclusion = conclusion,
        number = number,
        startedAtDate = startedAt?.let { parseLocalDateTime(it) },
        completedAtDate = completedAt?.let { parseLocalDateTime(it) },
    )

    fun isFinished() = completedAtDate != null
}

