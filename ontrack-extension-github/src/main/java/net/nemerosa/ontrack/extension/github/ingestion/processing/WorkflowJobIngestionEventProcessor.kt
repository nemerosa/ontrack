package net.nemerosa.ontrack.extension.github.ingestion.processing

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class WorkflowJobIngestionEventProcessor(
    structureService: StructureService,
) : AbstractWorkflowIngestionEventProcessor<WorkflowJobPayload>(
    structureService
) {

    override val event: String = "workflow_job"

    override val payloadType: KClass<WorkflowJobPayload> = WorkflowJobPayload::class

    override fun process(payload: WorkflowJobPayload) {
        // Build creation & setup
        // val build = getOrCreateBuild(payload)
        // TODO Build run info on job completed
        // TODO Build link to the GitHub job
        // TODO Build link to the GitHub job - status depending on the job action
        // TODO Validation stamps & run --> for the run
        // TODO Validation runs links to the GitHub workflow --> for the run
        // TODO Validation runs run info --> for the run
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class WorkflowJobPayload(
    val action: WorkflowJobAction,
    repository: Repository
) : AbstractWorkflowPayload(
    repository,
)

enum class WorkflowJobAction {
    queued,
    in_progress,
    completed
}
