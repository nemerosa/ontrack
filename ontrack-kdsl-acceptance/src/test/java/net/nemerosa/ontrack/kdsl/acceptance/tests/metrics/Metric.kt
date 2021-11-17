package net.nemerosa.ontrack.kdsl.acceptance.tests.metrics

/**
 * Metric
 */
data class Metric(
    val name: String,
    val type: String,
    val help: String,
    val values: List<MetricValue>,
) {
    fun getValue(vararg tags: Pair<String, String>): Int? {
        val value = values.find {
            tags.isEmpty() || it.tags.map { (name, value) -> name to value }.containsAll(tags.toList())
        }
        return value?.value?.toInt()
    }
}