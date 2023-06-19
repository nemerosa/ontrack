package net.nemerosa.ontrack.kdsl.spec.extension.queue

import java.time.LocalDateTime

data class QueueRecordHistory(
    val state: QueueRecordState,
    val time: LocalDateTime,
)