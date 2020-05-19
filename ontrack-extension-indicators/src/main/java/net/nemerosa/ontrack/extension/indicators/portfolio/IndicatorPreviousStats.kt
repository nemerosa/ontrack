package net.nemerosa.ontrack.extension.indicators.portfolio

import java.time.Duration

class IndicatorPreviousStats(
        val stats: IndicatorStats,
        val period: Duration,
        val minTrend: IndicatorTrend?,
        val avgTrend: IndicatorTrend?,
        val maxTrend: IndicatorTrend?
)