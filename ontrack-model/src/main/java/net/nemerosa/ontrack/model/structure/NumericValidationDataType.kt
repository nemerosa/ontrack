package net.nemerosa.ontrack.model.structure

interface NumericValidationDataType<C, T> : ValidationDataType<C, T> {

    /**
     * Gets some metrics about this data. Return `null` if not applicable.
     */
    fun getNumericMetrics(data: T): Map<String, Double>

}
