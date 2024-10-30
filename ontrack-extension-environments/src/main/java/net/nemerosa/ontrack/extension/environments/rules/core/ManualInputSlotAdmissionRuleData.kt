package net.nemerosa.ontrack.extension.environments.rules.core

import com.fasterxml.jackson.databind.JsonNode

data class ManualInputSlotAdmissionRuleData(
    val items: List<ManualInputSlotAdmissionRuleDataItem>,
) {
    fun findFieldValue(name: String): JsonNode? =
        items.find { it.name == name }?.value
}

data class ManualInputSlotAdmissionRuleDataItem(
    val name: String,
    val value: JsonNode?,
)
