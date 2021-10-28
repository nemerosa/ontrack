package net.nemerosa.ontrack.extension.github.ingestion.processing

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class WorkflowJobIngestionEventProcessor : AbstractIngestionEventProcessor<WorkflowJobPayload>() {

    override val event: String = "workflow-job"

    override val payloadType: KClass<WorkflowJobPayload> = WorkflowJobPayload::class

    override fun process(payload: WorkflowJobPayload) {
        TODO("Not yet implemented")
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class WorkflowJobPayload(
    val action: WorkflowJobAction,
)

enum class WorkflowJobAction {
    queued,
    in_progress,
    completed
}
