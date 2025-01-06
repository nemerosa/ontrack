package net.nemerosa.ontrack.extension.environments

import com.fasterxml.jackson.databind.JsonNode
import java.time.LocalDateTime

data class SlotAdmissionRuleData(
    val user: String,
    val timestamp: LocalDateTime,
    val data: JsonNode,
)
