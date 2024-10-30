package net.nemerosa.ontrack.extension.environments.rules.core

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.environments.SlotPipelineDataInputValue

data class ManualInputSlotAdmissionRuleData(
    val items: List<SlotPipelineDataInputValue>,
) {
    fun findFieldValue(name: String): JsonNode? =
        items.find { it.name == name }?.value
}
