package net.nemerosa.ontrack.extension.indicators.stats

/**
 * Trend for an indicator
 */
enum class IndicatorTrend {

    /**
     * Indicator quality has increased
     */
    GROWTH,

    /**
     * Indicator quality has stayed the same
     */
    SAME,
    /**
     * Indicator quality has decreased
     */
    DECREASE

}