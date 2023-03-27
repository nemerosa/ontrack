package net.nemerosa.ontrack.extension.queue.record

import net.nemerosa.ontrack.model.annotations.APIDescription
import java.time.LocalDateTime

@APIDescription("Change of state for a queue message")
data class QueueRecordHistory(
    @APIDescription("State at this time")
    val state: QueueRecordState,
    @APIDescription("Time for the state change")
    val time: LocalDateTime,
)