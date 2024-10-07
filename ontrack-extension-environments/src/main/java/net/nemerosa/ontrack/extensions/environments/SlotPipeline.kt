package net.nemerosa.ontrack.extensions.environments

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.model.structure.Build
import java.time.LocalDateTime
import java.util.*

data class SlotPipeline(
    val id: String = UUID.randomUUID().toString(),
    val start: LocalDateTime = Time.now,
    val end: LocalDateTime? = null,
    val status: SlotPipelineStatus = SlotPipelineStatus.ONGOING,
    val build: Build,
)
