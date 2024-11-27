package net.nemerosa.ontrack.extension.environments.workflows

import net.nemerosa.ontrack.extension.environments.Slot
import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.model.events.Event

interface SlotWorkflowService {

    fun addSlotWorkflow(slotWorkflow: SlotWorkflow)
    fun getSlotWorkflowsBySlot(slot: Slot): List<SlotWorkflow>
    fun getSlotWorkflowsBySlotAndTrigger(slot: Slot, trigger: SlotWorkflowTrigger): List<SlotWorkflow>

    fun startWorkflow(
        pipeline: SlotPipeline,
        slotWorkflow: SlotWorkflow,
        event: Event,
    ): SlotWorkflowInstance

    fun getSlotWorkflowInstancesByPipeline(pipeline: SlotPipeline): List<SlotWorkflowInstance>

    fun findSlotWorkflowById(id: String): SlotWorkflow?
    fun getSlotWorkflowById(id: String): SlotWorkflow

    fun findSlotWorkflowInstanceById(id: String): SlotWorkflowInstance?
    fun getSlotWorkflowInstanceById(id: String): SlotWorkflowInstance

    fun startWorkflowsForPipeline(
        pipeline: SlotPipeline,
        trigger: SlotWorkflowTrigger,
        event: Event,
    )

    fun findSlotWorkflowInstanceByPipelineAndSlotWorkflow(
        pipeline: SlotPipeline,
        slotWorkflow: SlotWorkflow
    ): SlotWorkflowInstance?

    fun deleteSlotWorkflow(slotWorkflow: SlotWorkflow)
    fun updateSlotWorkflow(slotWorkflow: SlotWorkflow)

}