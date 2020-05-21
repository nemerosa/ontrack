package net.nemerosa.ontrack.extension.indicators.portfolio

import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategory
import net.nemerosa.ontrack.extension.indicators.stats.IndicatorPreviousStats
import net.nemerosa.ontrack.extension.indicators.stats.IndicatorStats

/**
 * Association of several statuses for a category.
 */
class IndicatorCategoryStats(
        val category: IndicatorCategory,
        val stats: IndicatorStats,
        val previousStats: IndicatorPreviousStats?
)
