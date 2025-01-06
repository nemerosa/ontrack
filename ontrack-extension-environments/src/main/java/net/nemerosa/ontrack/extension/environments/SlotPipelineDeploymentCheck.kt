package net.nemerosa.ontrack.extension.environments

import com.fasterxml.jackson.databind.JsonNode

data class SlotPipelineDeploymentCheck(
    val check: DeployableCheck,
    val config: SlotAdmissionRuleConfig,
    val ruleData: JsonNode?,
    val override: SlotAdmissionRuleOverride? = null,
) {
    fun withOverride(override: SlotAdmissionRuleOverride) = SlotPipelineDeploymentCheck(
        check = check,
        config = config,
        ruleData = ruleData,
        override = override
    )
}
