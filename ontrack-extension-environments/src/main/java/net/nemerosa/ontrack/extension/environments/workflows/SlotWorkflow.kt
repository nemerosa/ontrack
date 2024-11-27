package net.nemerosa.ontrack.extension.environments.workflows

import net.nemerosa.ontrack.extension.environments.Slot
import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.model.annotations.APIDescription
import java.util.*

data class SlotWorkflow(
    @APIDescription("ID for this workflow")
    val id: String = UUID.randomUUID().toString(),
    @APIDescription("Slot this workflow is registered for")
    val slot: Slot,
    @APIDescription("Event which triggers this workflow")
    val trigger: SlotWorkflowTrigger,
    @APIDescription("Workflow to run")
    val workflow: Workflow,
    @APIDescription("(test only) Pause before running the worflow")
    val pauseMs: Long = 0,
) {
    fun withTrigger(trigger: SlotWorkflowTrigger) = SlotWorkflow(
        id = id,
        slot = slot,
        trigger = trigger,
        workflow = workflow,
        pauseMs = pauseMs,
    )

    fun withWorkflow(workflow: Workflow) = SlotWorkflow(
        id = id,
        slot = slot,
        trigger = trigger,
        workflow = workflow,
        pauseMs = pauseMs,
    )
}
