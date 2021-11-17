package net.nemerosa.ontrack.kdsl.acceptance.tests.metrics

/**
 * Metric
 */
data class Metric(
    val name: String,
    val type: String,
    val help: String,
    val values: List<MetricValue>,
)