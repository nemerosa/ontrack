package net.nemerosa.ontrack.extension.queue.record

import java.time.LocalDateTime

data class QueueRecordHistory(
    val state: QueueRecordState,
    val time: LocalDateTime,
)