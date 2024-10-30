package net.nemerosa.ontrack.extension.environments

data class SlotAdmissionRuleInput(
    val config: SlotAdmissionRuleConfig,
    val fields: List<SlotAdmissionRuleInputField>,
)
