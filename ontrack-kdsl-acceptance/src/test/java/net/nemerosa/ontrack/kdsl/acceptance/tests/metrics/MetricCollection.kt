package net.nemerosa.ontrack.kdsl.acceptance.tests.metrics

/**
 * List of metrics
 */
data class MetricCollection(
    val list: List<Metric>,
) {
    /**
     * Gets the value for a counter
     *
     * @param name Name of the counter metric
     * @param tags Tags to include
     * @return Value of the counter or null if not defined
     */
    fun getCounter(
        name: String,
        vararg tags: Pair<String, String>,
    ): Int? {
        val counter = list.find {
            it.name == name && it.type == "counter"
        } ?: return null
        return counter.getValue(*tags)
    }
}