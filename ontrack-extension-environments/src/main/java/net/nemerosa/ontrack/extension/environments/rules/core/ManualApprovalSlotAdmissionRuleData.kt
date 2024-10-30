package net.nemerosa.ontrack.extension.environments.rules.core

import net.nemerosa.ontrack.model.annotations.APILabel

data class ManualApprovalSlotAdmissionRuleData(
    @APILabel("Approval")
    val approval: Boolean,
    @APILabel("Approval message")
    val message: String?,
)
