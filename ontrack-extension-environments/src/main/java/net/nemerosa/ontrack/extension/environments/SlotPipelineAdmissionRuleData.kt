package net.nemerosa.ontrack.extension.environments

import java.time.LocalDateTime

data class SlotPipelineAdmissionRuleData<D>(
    val timestamp: LocalDateTime,
    val user: String,
    val data: D?,
)
