package net.nemerosa.ontrack.model.job

import java.time.Duration
import java.time.LocalDateTime

data class JobHistogram(
    val from: LocalDateTime,
    val to: LocalDateTime,
    val interval: Duration,
    val items: List<JobHistogramItem>,
)