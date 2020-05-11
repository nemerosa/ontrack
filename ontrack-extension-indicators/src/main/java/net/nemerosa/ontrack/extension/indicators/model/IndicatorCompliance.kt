package net.nemerosa.ontrack.extension.indicators.model

data class IndicatorCompliance(
        val value: Int
) : Comparable<IndicatorCompliance> {
    init {
        check(value in 0..100) { "Indicator compliance value must be >= 0 and <= 100." }
    }

    override fun compareTo(other: IndicatorCompliance): Int = compareValues(this.value, other.value)

    companion object {

        private const val MAX = 100
        private const val MIN = 0

        val HIGHEST = IndicatorCompliance(MAX)
        val MEDIUM = IndicatorCompliance((MAX + MIN) / 2)
        val LOWEST = IndicatorCompliance(MIN)

    }

}