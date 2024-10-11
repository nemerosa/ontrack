package net.nemerosa.ontrack.extensions.environments

import com.fasterxml.jackson.databind.JsonNode
import java.util.*

/**
 * Configuration of an admission rule for a slot.
 */
data class SlotAdmissionRuleConfig(
    val id: String = UUID.randomUUID().toString(),
    val slot: Slot,
    val name: String,
    val description: String?,
    val ruleId: String,
    val ruleConfig: JsonNode,
)
