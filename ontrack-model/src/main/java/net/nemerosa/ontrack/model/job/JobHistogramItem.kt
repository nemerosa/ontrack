package net.nemerosa.ontrack.model.job

import java.time.LocalDateTime

data class JobHistogramItem(
    val from: LocalDateTime,
    val to: LocalDateTime,
    val count: Int,
    val errorCount: Int,
    val avgDurationMs: Long,
) {
    val error = errorCount > 0
}
