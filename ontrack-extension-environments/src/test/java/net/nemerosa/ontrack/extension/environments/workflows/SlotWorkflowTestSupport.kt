package net.nemerosa.ontrack.extension.environments.workflows

import net.nemerosa.ontrack.extension.environments.Slot
import net.nemerosa.ontrack.extension.environments.SlotTestSupport
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.springframework.stereotype.Component

@Component
class SlotWorkflowTestSupport(
    private val slotTestSupport: SlotTestSupport,
    private val slotWorkflowService: SlotWorkflowService,
) : AbstractDSLTestSupport() {

    fun withSlotWorkflow(
        trigger: SlotWorkflowTrigger,
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