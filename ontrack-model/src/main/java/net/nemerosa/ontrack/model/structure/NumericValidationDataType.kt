package net.nemerosa.ontrack.model.structure

interface NumericValidationDataType<C, T> : ValidationDataType<C, T> {

    /**
     * Ordered list of metrics
     *
     * @return List of metrics or null if they have to be determined dynamically
     */
    fun getMetricNames(): List<String>?

    /**
     * Optional list of colors
     *
     * @return Color palette for the metrics (if not a default)
     */
    fun getMetricColors(): List<String>? = null

    /**
     * Gets some metrics about this data.
     */
    fun getNumericMetrics(data: T): Map<String, Double>

}
