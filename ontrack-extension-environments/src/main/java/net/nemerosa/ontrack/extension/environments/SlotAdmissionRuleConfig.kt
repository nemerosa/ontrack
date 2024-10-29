package net.nemerosa.ontrack.extension.environments

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
) {
    fun withDescription(description: String?) = SlotAdmissionRuleConfig(
        id = id,
        slot = slot,
        name = name,
        description = description,
        ruleId = ruleId,
        ruleConfig = ruleConfig
    )

    fun withRuleConfig(ruleConfig: JsonNode) = SlotAdmissionRuleConfig(
        id = id,
        slot = slot,
        name = name,
        description = description,
        ruleId = ruleId,
        ruleConfig = ruleConfig
    )
}
