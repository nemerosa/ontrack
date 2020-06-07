package net.nemerosa.ontrack.extension.indicators.support

data class PercentageThreshold(
        val threshold: Percentage,
        val higherIsBetter: Boolean = true
) {

    /**
     * Gets the compliance of a percentage according to this range.
     */
    fun getCompliance(value: Percentage): Percentage =
            if (higherIsBetter) {
                getCompliance(
                        threshold = threshold.value,
                        value = value.value
                ).percent()
            } else {
                getCompliance(
                        threshold = threshold.invert().value,
                        value = value.invert().value
                ).percent()
            }

    private fun getCompliance(threshold: Int, value: Int): Int =
            when {
                value >= threshold -> 100 // True if threshold = 0
                else -> (value * 100) / threshold
            }
}