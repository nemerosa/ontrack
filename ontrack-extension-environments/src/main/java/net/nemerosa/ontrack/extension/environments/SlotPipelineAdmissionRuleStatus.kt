package net.nemerosa.ontrack.extension.environments

data class SlotPipelineAdmissionRuleStatus(
    val pipeline: SlotPipeline,
    val admissionRuleConfig: SlotAdmissionRuleConfig,
    val data: SlotAdmissionRuleData? = null,
    val override: SlotAdmissionRuleOverride? = null,
)
