package net.nemerosa.ontrack.extension.indicators.portfolio

import net.nemerosa.ontrack.extension.indicators.model.IndicatorType
import net.nemerosa.ontrack.extension.indicators.stats.IndicatorStats

/**
 * Association of several statuses for a type.
 */
class IndicatorTypeStats(
        val type: IndicatorType<*, *>,
        val stats: IndicatorStats
)