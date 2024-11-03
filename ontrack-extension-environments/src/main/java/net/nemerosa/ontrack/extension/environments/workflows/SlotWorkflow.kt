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
)
