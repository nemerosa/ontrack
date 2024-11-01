package net.nemerosa.ontrack.extension.environments

import com.fasterxml.jackson.databind.JsonNode

data class SlotAdmissionRuleInput(
    val config: SlotAdmissionRuleConfig,
    val data: JsonNode?,
)
