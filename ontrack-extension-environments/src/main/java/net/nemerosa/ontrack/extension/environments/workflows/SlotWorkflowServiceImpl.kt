package net.nemerosa.ontrack.extension.environments.workflows

import net.nemerosa.ontrack.extension.environments.Slot
import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.extension.environments.security.SlotPipelineWorkflowRun
import net.nemerosa.ontrack.extension.environments.security.SlotUpdate
import net.nemerosa.ontrack.extension.environments.security.SlotView
import net.nemerosa.ontrack.extension.environments.service.checkSlotAccess
import net.nemerosa.ontrack.extension.environments.service.isSlotAccessible
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowContext
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowEngine
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SlotWorkflowServiceImpl(
    private val securityService: SecurityService,
    private val slotWorkflowRepository: SlotWorkflowRepository,
    private val slotWorkflowInstanceRepository: SlotWorkflowInstanceRepository,
    private val workflowEngine: WorkflowEngine,
) : SlotWorkflowService {

    override fun addSlotWorkflow(slotWorkflow: SlotWorkflow) {
        securityService.checkSlotAccess<SlotUpdate>(slotWorkflow.slot)
        slotWorkflowRepository.addSlotWorkflow(slotWorkflow)
    }

    override fun getSlotWorkflowsBySlotAndTrigger(slot: Slot, trigger: SlotWorkflowTrigger): List<SlotWorkflow> {
        securityService.checkSlotAccess<SlotView>(slot)
        return slotWorkflowRepository.getSlotWorkflowsBySlotAndTrigger(slot, trigger)
            .sortedBy { it.workflow.name }
    }

    override fun getSlotWorkflowsBySlot(slot: Slot): List<SlotWorkflow> {
        securityService.checkSlotAccess<SlotView>(slot)
        return slotWorkflowRepository.getSlotWorkflowsBySlot(slot)
            .sortedBy { it.workflow.name }
    }

    override fun startWorkflow(pipeline: SlotPipeline, slotWorkflow: SlotWorkflow): SlotWorkflowInstance {
        securityService.checkSlotAccess<SlotPipelineWorkflowRun>(pipeline.slot)
        if (pipeline.slot.id != slotWorkflow.slot.id) {
            error("Cannot start a workflow for a slot that is different than the one of the pipeline.")
        }
        // Starting the workflow
        val workflowInstance = workflowEngine.startWorkflow(
            workflow = slotWorkflow.workflow,
            context = WorkflowContext.noContext()
        ) { context, instanceId ->
            // TODO Slot/pipeline contributions to the context
            context
        }
        // Slot workflow instance record
        val slotWorkflowInstance = SlotWorkflowInstance(
            pipeline = pipeline,
            slotWorkflow = slotWorkflow,
            workflowInstance = workflowInstance,
        )
        // Saving the record
        slotWorkflowInstanceRepository.addSlotWorkflowInstance(slotWorkflowInstance)
        // Ok
        return slotWorkflowInstance
    }

    override fun startWorkflowsForPipeline(pipeline: SlotPipeline, trigger: SlotWorkflowTrigger) {
        securityService.checkSlotAccess<SlotPipelineWorkflowRun>(pipeline.slot)
        val slotWorkflows = getSlotWorkflowsBySlotAndTrigger(pipeline.slot, trigger)
        slotWorkflows.forEach { slotWorkflow ->
            startWorkflow(pipeline, slotWorkflow)
        }
    }

    override fun getSlotWorkflowInstancesByPipeline(pipeline: SlotPipeline): List<SlotWorkflowInstance> {
        securityService.checkSlotAccess<SlotView>(pipeline.slot)
        return slotWorkflowInstanceRepository.getSlotWorkflowInstancesByPipeline(pipeline)
    }

    override fun findSlotWorkflowInstanceById(id: String): SlotWorkflowInstance? =
        slotWorkflowInstanceRepository.findSlotWorkflowInstancesById(id)
            ?.takeIf { securityService.isSlotAccessible<SlotView>(it.pipeline.slot) }

    override fun getSlotWorkflowInstanceById(id: String): SlotWorkflowInstance =
        findSlotWorkflowInstanceById(id) ?: throw SlotWorkflowInstanceIdNotFoundException(id)

    override fun findSlotWorkflowInstanceByPipelineAndSlotWorkflow(
        pipeline: SlotPipeline,
        slotWorkflow: SlotWorkflow
    ): SlotWorkflowInstance? {
        securityService.checkSlotAccess<SlotView>(pipeline.slot)
        return slotWorkflowInstanceRepository.findSlotWorkflowInstanceByPipelineAndSlotWorkflow(
            pipeline,
            slotWorkflow,
        )
    }

    override fun deleteSlotWorkflow(slotWorkflow: SlotWorkflow) {
        securityService.checkSlotAccess<SlotUpdate>(slotWorkflow.slot)
        slotWorkflowRepository.deleteSlotWorkflow(slotWorkflow)
    }

    override fun updateSlotWorkflow(slotWorkflow: SlotWorkflow) {
        securityService.checkSlotAccess<SlotUpdate>(slotWorkflow.slot)
        slotWorkflowRepository.updateSlotWorkflow(slotWorkflow)
    }
}