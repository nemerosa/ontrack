package net.nemerosa.ontrack.extension.indicators.support

data class IntegerThresholds(
        val min: Int,
        val max: Int,
        val higherIsBetter: Boolean = false
) {

    init {
        check (min >= 0) {
            "Min must be >= 0"
        }
        check(max > min) {
            "Max must be > min"
        }
    }

    /**
     * Gets the compliance of an integer according to this range.
     */
    fun getCompliance(value: Int): Percentage =
        if (higherIsBetter) {
            when {
                value >= max -> 100.percent()
                value < min -> 0.percent()
                else -> ((value - min) * 100 / (max - min)).percent()
            }
        } else {
            when {
                value > max -> 0.percent()
                value <= min -> 100.percent()
                else -> ((value - min) * 100 / (max - min)).percent().invert()
            }
        }

}