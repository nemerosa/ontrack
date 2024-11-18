package net.nemerosa.ontrack.extension.environments.workflows.executors

import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.extension.environments.workflows.SlotWorkflow
import net.nemerosa.ontrack.model.events.SerializableEvent

object SlotWorkflowContext {
    const val EVENT_SLOT_PIPELINE_ID = "slotPipelineId"
    const val EVENT_SLOT_WORKFLOW_ID = "slotWorkflowId"
}

fun SerializableEvent.forSlotWorkflowExecution(
    pipeline: SlotPipeline,
    slotWorkflow: SlotWorkflow,
) = withValue(
    SlotWorkflowContext.EVENT_SLOT_PIPELINE_ID,
    pipeline.id
)
    .withValue(
        SlotWorkflowContext.EVENT_SLOT_WORKFLOW_ID,
        slotWorkflow.id
    )

fun SerializableEvent.forSlotPipeline(
    pipeline: SlotPipeline,
) = withValue(
    SlotWorkflowContext.EVENT_SLOT_PIPELINE_ID,
    pipeline.id
)
