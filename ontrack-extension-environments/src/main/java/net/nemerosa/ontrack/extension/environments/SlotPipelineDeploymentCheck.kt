package net.nemerosa.ontrack.extension.environments

import com.fasterxml.jackson.databind.JsonNode

data class SlotPipelineDeploymentCheck(
    val check: DeployableCheck,
    val ruleId: String,
    val ruleConfig: JsonNode,
    val ruleData: JsonNode?,
    val override: SlotPipelineAdmissionRuleStatus? = null,
) {
    fun withOverride(override: SlotPipelineAdmissionRuleStatus) = SlotPipelineDeploymentCheck(
        check = check,
        ruleId = ruleId,
        ruleConfig = ruleConfig,
        ruleData = ruleData,
        override = override
    )
}
