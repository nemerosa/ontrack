package net.nemerosa.ontrack.extension.environments.rules.core

import net.nemerosa.ontrack.extension.environments.SlotAdmissionRuleInputFieldType

data class ManualInputSlotAdmissionRuleConfig(
    val fields: List<ManualInputSlotAdmissionRuleConfigField>,
)

data class ManualInputSlotAdmissionRuleConfigField(
    val name: String,
    val type: SlotAdmissionRuleInputFieldType,
    val label: String,
)
