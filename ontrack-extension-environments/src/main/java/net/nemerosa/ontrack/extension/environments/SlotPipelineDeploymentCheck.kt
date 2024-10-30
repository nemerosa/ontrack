package net.nemerosa.ontrack.extension.environments

import com.fasterxml.jackson.databind.JsonNode

data class SlotPipelineDeploymentCheck(
    val check: DeployableCheck,
    val config: SlotAdmissionRuleConfig,
    val ruleData: JsonNode?,
    val override: SlotPipelineAdmissionRuleStatus? = null,
) {
    fun withOverride(override: SlotPipelineAdmissionRuleStatus) = SlotPipelineDeploymentCheck(
        check = check,
        config = config,
        ruleData = ruleData,
        override = override
    )
}
