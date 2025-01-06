package net.nemerosa.ontrack.extension.environments

import java.time.LocalDateTime
import java.util.*

data class SlotPipelineChange(
    val id: String = UUID.randomUUID().toString(),
    val pipeline: SlotPipeline,
    val user: String,
    val timestamp: LocalDateTime,
    val status: SlotPipelineStatus?,
    val message: String,
    val dataChanged: Boolean = false,
    val overridden: Boolean = false,
    val overrideMessage: String? = null,
)
