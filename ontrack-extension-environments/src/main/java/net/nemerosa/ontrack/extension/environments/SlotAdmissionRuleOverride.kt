package net.nemerosa.ontrack.extension.environments

import java.time.LocalDateTime

data class SlotAdmissionRuleOverride(
    val user: String,
    val timestamp: LocalDateTime,
    val message: String?,
)
