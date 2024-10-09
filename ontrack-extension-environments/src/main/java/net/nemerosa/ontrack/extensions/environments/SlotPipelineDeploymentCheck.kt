package net.nemerosa.ontrack.extensions.environments

import com.fasterxml.jackson.databind.JsonNode

data class SlotPipelineDeploymentCheck(
    val status: Boolean,
    val ruleId: String,
    val ruleConfig: JsonNode,
)
