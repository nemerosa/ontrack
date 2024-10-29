package net.nemerosa.ontrack.extension.environments.casc

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.environments.Slot

data class EnvironmentsCascModel(
    val keepEnvironments: Boolean = true,
    val environments: List<EnvironmentCasc>,
    val slots: List<SlotCasc> = emptyList(),
)

data class EnvironmentCasc(
    val name: String,
    val description: String = "",
    val order: Int,
    val tags: List<String> = emptyList(),
)

data class SlotCasc(
    val project: String,
    val qualifier: String = Slot.DEFAULT_QUALIFIER,
    val description: String = "",
    val environments: List<SlotEnvironmentCasc>,
)

data class SlotEnvironmentCasc(
    val name: String,
    val admissionRules: List<SlotEnvironmentAdmissionRuleCasc> = emptyList(),
)

data class SlotEnvironmentAdmissionRuleCasc(
    val name: String? = null,
    val description: String = "",
    val ruleId: String,
    val ruleConfig: JsonNode,
) {
    @get:JsonIgnore
    val actualName: String = name ?: ruleId
}
