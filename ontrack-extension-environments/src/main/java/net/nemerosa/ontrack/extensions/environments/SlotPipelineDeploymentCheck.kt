package net.nemerosa.ontrack.extensions.environments

import com.fasterxml.jackson.databind.JsonNode

data class SlotPipelineDeploymentCheck(
    val status: Boolean,
    val ruleId: String,
    val ruleConfig: JsonNode,
    val ruleData: JsonNode?,
    val override: SlotPipelineAdmissionRuleStatus? = null,
) {
    fun withOverride(override: SlotPipelineAdmissionRuleStatus) = SlotPipelineDeploymentCheck(
        status = status,
        ruleId = ruleId,
        ruleConfig = ruleConfig,
        ruleData = ruleData,
        override = override
    )
}
