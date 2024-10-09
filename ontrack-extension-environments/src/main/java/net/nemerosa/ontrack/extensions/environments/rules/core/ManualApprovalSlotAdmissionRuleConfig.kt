package net.nemerosa.ontrack.extensions.environments.rules.core

/**
 * @property message Message to display to the approver
 */
data class ManualApprovalSlotAdmissionRuleConfig(
    val message: String,
    val users: List<String> = emptyList(),
    val groups: List<String> = emptyList(),
)