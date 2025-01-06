package net.nemerosa.ontrack.extension.environments.workflows

import net.nemerosa.ontrack.extension.environments.Slot
import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.extension.environments.SlotPipelineStatus
import net.nemerosa.ontrack.extension.environments.SlotTestSupport
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.waitUntil
import org.springframework.stereotype.Component
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@Component
class SlotWorkflowTestSupport(
    private val slotTestSupport: SlotTestSupport,
    private val slotWorkflowService: SlotWorkflowService,
) : AbstractDSLTestSupport() {

    fun waitForSlotWorkflowsToFinish(
        pipeline: SlotPipeline,
        trigger: SlotPipelineStatus,
        timeout: Duration = 10.seconds,
    ) {
        val slotWorkflowInstances = slotWorkflowService.getSlotWorkflowInstancesByPipeline(pipeline)
            .filter { it.slotWorkflow.trigger == trigger }
        slotWorkflowInstances.forEach { slotWorkflowInstance ->
            waitForSlotWorkflowInstanceToFinish(slotWorkflowInstance, timeout)
        }
    }

    @OptIn(ExperimentalTime::class)
    fun waitForSlotWorkflowInstanceToFinish(
        slotWorkflowInstance: SlotWorkflowInstance,
        timeout: Duration = 10.seconds,
    ) {
        waitUntil(
            message = "Slot workflow instance to finish: ${slotWorkflowInstance.id}",
            interval = 1.seconds,
            timeout = timeout,
        ) {
            slotWorkflowService.getSlotWorkflowInstanceById(slotWorkflowInstance.id).workflowInstance.status.finished
        }
    }

    fun withSlotWorkflow(
        trigger: SlotPipelineStatus,
        waitMs: Int = 0,
        error: Boolean = false,
        code: (slot: Slot, slotWorkflow: SlotWorkflow) -> Unit
    ) {
        slotTestSupport.withSlot { slot ->

            val testWorkflow = SlotWorkflowTestFixtures.testWorkflow(
                waitMs = waitMs,
                error = error,
            )
            val slotWorkflow = SlotWorkflow(
                slot = slot,
                trigger = trigger,
                workflow = testWorkflow,
            )
            slotWorkflowService.addSlotWorkflow(slotWorkflow)

            code(slot, slotWorkflow)
        }
    }

}