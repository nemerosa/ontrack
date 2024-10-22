package net.nemerosa.ontrack.extension.environments.rules.core

/**
 * @property message Message to display to the approver
 */
data class ManualApprovalSlotAdmissionRuleConfig(
    val message: String,
    val users: List<String> = emptyList(),
    val groups: List<String> = emptyList(),
)