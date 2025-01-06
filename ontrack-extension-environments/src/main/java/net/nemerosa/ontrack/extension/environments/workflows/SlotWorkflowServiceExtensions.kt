package net.nemerosa.ontrack.extension.environments.workflows

import net.nemerosa.ontrack.extension.environments.SlotAdmissionRuleConfig
import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.json.parse

fun SlotWorkflowService.overrideSlotWorkflowInstance(
    pipeline: SlotPipeline,
    admissionRuleConfig: SlotAdmissionRuleConfig,
    message: String
) {
    val config = admissionRuleConfig.ruleConfig.parse<WorkflowSlotAdmissionRuleConfig>()
    if (config.slotWorkflowInstanceId != null) {
        overrideSlotWorkflowInstance(
            pipeline = pipeline,
            slotWorkflowId = config.slotWorkflowId,
            slotWorkflowInstanceId = config.slotWorkflowInstanceId,
            message = message,
        )
    }
}