package net.nemerosa.ontrack.extension.environments

import java.util.*

data class SlotPipelineAdmissionRuleStatus(
    val id: String = UUID.randomUUID().toString(),
    val pipeline: SlotPipeline,
    val admissionRuleConfig: SlotAdmissionRuleConfig,
    val data: SlotAdmissionRuleData? = null,
    val override: SlotAdmissionRuleOverride? = null,
)
