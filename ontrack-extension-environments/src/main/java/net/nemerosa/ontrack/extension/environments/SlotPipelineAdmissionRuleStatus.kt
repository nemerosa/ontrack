package net.nemerosa.ontrack.extension.environments

import com.fasterxml.jackson.databind.JsonNode
import java.time.LocalDateTime
import java.util.*

data class SlotPipelineAdmissionRuleStatus(
    val id: String = UUID.randomUUID().toString(),
    val pipeline: SlotPipeline,
    val admissionRuleConfig: SlotAdmissionRuleConfig,
    val timestamp: LocalDateTime,
    val user: String,
    val data: JsonNode?,
    val override: Boolean,
    val overrideMessage: String?,
)
