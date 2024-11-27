package net.nemerosa.ontrack.extension.environments.workflows.executors

import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.extension.environments.workflows.SlotWorkflow
import net.nemerosa.ontrack.model.events.SerializableEvent
import net.nemerosa.ontrack.model.events.withBuild

object SlotWorkflowContext {
    const val EVENT_SLOT_PIPELINE_ID = "slotPipelineId"
    const val EVENT_SLOT_WORKFLOW_ID = "slotWorkflowId"
}

fun SerializableEvent.findSlotWorkflowId() = findValue(SlotWorkflowContext.EVENT_SLOT_WORKFLOW_ID)

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
    .withBuild(pipeline.build)

fun SerializableEvent.forSlotPipeline(
    pipeline: SlotPipeline,
) =
    withValue(
        SlotWorkflowContext.EVENT_SLOT_PIPELINE_ID,
        pipeline.id
    )
        .withBuild(pipeline.build)
