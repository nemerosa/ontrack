package net.nemerosa.ontrack.extension.github.ingestion.processing.events

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
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
) : AbstractWorkflowIngestionEventProcessor<WorkflowJobPayload>(
    structureService
) {

    override val event: String = "workflow_job"

    override val payloadType: KClass<WorkflowJobPayload> = WorkflowJobPayload::class

    override fun process(payload: WorkflowJobPayload) {
        when (payload.action) {
            WorkflowJobAction.in_progress -> onJob(payload, finished = false)
            WorkflowJobAction.completed -> onJob(payload, finished = true)
            else -> {
            } // Nothing
        }
    }

    private fun onJob(payload: WorkflowJobPayload, finished: Boolean) {
        // Processes each step independently
        payload.steps?.forEach { step ->
            onStep(step, payload, finished)
        }
    }

    private fun onStep(step: WorkflowJobStep, payload: WorkflowJobPayload, finished: Boolean) {
        workflowJobProcessingService.setupValidation(
            owner = payload.repository.owner.login,
            repository = payload.repository.name,
            runId = payload.runId,
            runAttempt = payload.runAttempt,
            job = payload.name,
            step = step.name,
            status = step.status,
            conclusion = step.conclusion,
            startedAt = step.startedAtDate,
            completedAt = step.completedAtDate,
        )
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class WorkflowJobPayload(
    val runId: Long,
    val runAttempt: Int,
    val action: WorkflowJobAction,
    repository: Repository,
    val status: WorkflowJobStepStatus,
    val conclusion: WorkflowJobStepConclusion?,
    val startedAtDate: LocalDateTime?,
    val completedAtDate: LocalDateTime?,
    val name: String,
    val steps: List<WorkflowJobStep>?,
) : AbstractWorkflowPayload(
    repository,
) {
    @JsonCreator
    constructor(
        @JsonProperty("run_id")
        runId: Long,
        @JsonProperty("run_attempt")
        runAttempt: Int,
        action: WorkflowJobAction,
        repository: Repository,
        status: WorkflowJobStepStatus,
        conclusion: WorkflowJobStepConclusion?,
        @JsonProperty("started_at")
        startedAt: String?,
        @JsonProperty("completed_at")
        completedAt: String?,
        name: String,
        steps: List<WorkflowJobStep>?,
    ) : this(
        runId = runId,
        runAttempt = runAttempt,
        action = action,
        repository = repository,
        status = status,
        conclusion = conclusion,
        startedAtDate = startedAt?.let { parseLocalDateTime(it) },
        completedAtDate = completedAt?.let { parseLocalDateTime(it) },
        name = name,
        steps = steps
    )
}

@Suppress("EnumEntryName")
enum class WorkflowJobAction {
    queued,
    in_progress,
    completed,
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

