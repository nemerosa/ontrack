package net.nemerosa.ontrack.extension.av.graphql

import net.nemerosa.ontrack.common.api.APIDescription
import java.time.LocalDateTime

@APIDescription("Scheduling the auto-versioning for a given time (or current time)")
data class ScheduleAutoVersioningInput(
    @APIDescription("Time at which to schedule the auto-versioning")
    val time: LocalDateTime? = null,
)