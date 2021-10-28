package net.nemerosa.ontrack.extension.github.ingestion.processing

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class WorkflowRunIngestionEventProcessor(
    structureService: StructureService,
) : AbstractWorkflowIngestionEventProcessor<WorkflowRunPayload>(
    structureService
) {

    override val event: String = "workflow-run"

    override val payloadType: KClass<WorkflowRunPayload> = WorkflowRunPayload::class

    override fun process(payload: WorkflowRunPayload) {
        // Build creation & setup
        val build = getOrCreateBuild(payload)
        // TODO Validation stamps & run --> for the run
        // TODO Validation runs links to the GitHub workflow --> for the run
        // TODO Validation runs run info --> for the run
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class WorkflowRunPayload(
    repository: Repository,
) : AbstractWorkflowPayload(
    repository,
)
