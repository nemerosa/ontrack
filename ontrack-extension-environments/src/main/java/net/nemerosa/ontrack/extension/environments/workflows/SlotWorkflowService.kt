package net.nemerosa.ontrack.extension.environments.workflows

import net.nemerosa.ontrack.extension.environments.*
import net.nemerosa.ontrack.model.events.Event

interface SlotWorkflowService {

    fun addSlotWorkflow(slotWorkflow: SlotWorkflow)
    fun getSlotWorkflowsBySlot(slot: Slot): List<SlotWorkflow>
    fun getSlotWorkflowsBySlotAndTrigger(slot: Slot, trigger: SlotPipelineStatus): List<SlotWorkflow>

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
        trigger: SlotPipelineStatus,
        event: Event,
    )

    fun findSlotWorkflowInstanceByPipelineAndSlotWorkflow(
        pipeline: SlotPipeline,
        slotWorkflow: SlotWorkflow
    ): SlotWorkflowInstance?

    fun deleteSlotWorkflow(slotWorkflow: SlotWorkflow)
    fun updateSlotWorkflow(slotWorkflow: SlotWorkflow)

    fun overrideSlotWorkflowInstance(
        slotWorkflowInstanceId: String,
        message: String,
    ): SlotAdmissionRuleOverride

    /**
     * Gets the check results for all the workflows of a pipeline for a given trigger.
     */
    fun getSlotWorkflowChecks(pipeline: SlotPipeline, trigger: SlotPipelineStatus, skipWorkflowId: String?): List<SlotDeploymentCheck>

}