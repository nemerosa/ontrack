package net.nemerosa.ontrack.model.metrics

import java.time.LocalDateTime

data class Metric(
    val metric: String,
    val tags: Map<String, String>,
    val fields: Map<String, *>,
    val timestamp: LocalDateTime,
)
