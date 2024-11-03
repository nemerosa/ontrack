package net.nemerosa.ontrack.extension.environments.workflows

import net.nemerosa.ontrack.extension.environments.Slot
import net.nemerosa.ontrack.extension.environments.SlotPipeline

interface SlotWorkflowService {

    fun addSlotWorkflow(slotWorkflow: SlotWorkflow)
    fun getSlotWorkflowsBySlot(slot: Slot, trigger: SlotWorkflowTrigger): List<SlotWorkflow>

    fun startWorkflow(pipeline: SlotPipeline, slotWorkflow: SlotWorkflow): SlotWorkflowInstance
    fun getSlotWorkflowInstancesByPipeline(pipeline: SlotPipeline): List<SlotWorkflowInstance>

    fun findSlotWorkflowInstanceById(id: String): SlotWorkflowInstance?
    fun getSlotWorkflowInstanceById(id: String): SlotWorkflowInstance

    fun startWorkflowsForPipeline(pipeline: SlotPipeline, trigger: SlotWorkflowTrigger)

    fun findSlotWorkflowInstanceByPipelineAndSlotWorkflow(
        pipeline: SlotPipeline,
        slotWorkflow: SlotWorkflow
    ): SlotWorkflowInstance?

}