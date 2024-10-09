package net.nemerosa.ontrack.extensions.environments

import java.time.LocalDateTime

data class SlotPipelineAdmissionRuleData<D>(
    val timestamp: LocalDateTime,
    val user: String,
    val data: D?,
)
