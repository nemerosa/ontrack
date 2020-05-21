package net.nemerosa.ontrack.extension.indicators.stats

import net.nemerosa.ontrack.extension.indicators.stats.IndicatorStats
import net.nemerosa.ontrack.extension.indicators.stats.IndicatorTrend
import java.time.Duration

class IndicatorPreviousStats(
        val stats: IndicatorStats,
        val period: Duration,
        val minTrend: IndicatorTrend?,
        val avgTrend: IndicatorTrend?,
        val maxTrend: IndicatorTrend?
)