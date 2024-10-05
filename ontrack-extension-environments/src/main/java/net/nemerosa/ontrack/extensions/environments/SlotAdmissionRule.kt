package net.nemerosa.ontrack.extensions.environments

import com.fasterxml.jackson.databind.JsonNode

data class SlotAdmissionRule(
    val id: Int,
    val name: String,
    val description: String?,
    val ruleId: String,
    val ruleConfig: JsonNode,
)
