package net.nemerosa.ontrack.extension.environments.workflows

class WorkflowSlotAdmissionRule {

    companion object {
        const val ID = "workflow"

        fun getConfig(slotWorkflow: SlotWorkflow, slotWorkflowInstance: SlotWorkflowInstance?) = WorkflowSlotAdmissionRuleConfig(
            slotWorkflowId = slotWorkflow.id,
            slotWorkflowInstanceId = slotWorkflowInstance?.id,
        )
    }

}