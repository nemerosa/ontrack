package net.nemerosa.ontrack.model.structure

interface NumericValidationDataType<C, T> : ValidationDataType<C, T> {

    /**
     * Ordered list of metrics
     *
     * @return List of metrics or null if they have to be determined dynamically
     */
    fun getMetricNames(): List<String>?

    /**
     * Gets some metrics about this data.
     */
    fun getNumericMetrics(data: T): Map<String, Double>

}
