package net.nemerosa.ontrack.extension.environments

data class SlotAdmissionRuleInputField(
    val type: SlotAdmissionRuleInputFieldType,
    val name: String,
    val label: String,
    val value: Any?,
)
