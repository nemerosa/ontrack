package net.nemerosa.ontrack.model.job

import java.time.Duration
import java.time.LocalDateTime

data class JobHistoryItem(
    val id: Int,
    val jobCategory: String,
    val jobType: String,
    val jobKey: String,
    val startedAt: LocalDateTime,
    val endedAt: LocalDateTime,
    val status: JobHistoryItemStatus,
    val message: String?,
) {
    val duration: Duration = Duration.between(startedAt, endedAt)
}