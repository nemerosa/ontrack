package net.nemerosa.ontrack.extension.indicators.model

data class IndicatorStatus(
        val value: Int
) {
    init {
        check(value in 0..100) { "Indicator status value must be >= 0 and <= 100." }
    }

    companion object {

        const val MAX = 100
        const val MIN = 0

        val HIGHEST = IndicatorStatus(MAX)
        val MEDIUM = IndicatorStatus((MAX + MIN) / 2)
        val LOWEST = IndicatorStatus(MIN)

        fun inRange(value: Int) = value in MIN..MAX

    }

}