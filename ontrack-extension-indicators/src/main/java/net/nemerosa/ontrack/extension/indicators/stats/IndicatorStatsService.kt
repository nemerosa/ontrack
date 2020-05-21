package net.nemerosa.ontrack.extension.indicators.stats

import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategory
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorCategoryStats
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorPortfolio
import net.nemerosa.ontrack.model.structure.Project
import java.time.Duration

interface IndicatorStatsService {

    /**
     * Gets global stats for a portfolio
     */
    fun getGlobalStats(portfolio: IndicatorPortfolio, previous: Duration? = null): List<IndicatorCategoryStats>

    /**
     * Gets stats for a category and a project
     */
    fun getStatsForCategoryAndProject(category: IndicatorCategory, project: Project, previous: Duration? = null): IndicatorCategoryStats

    /**
     * Gets stats for a category and a portfolio
     */
    fun getStatsPortfolio(portfolio: IndicatorPortfolio, previous: Duration? = null): List<IndicatorCategoryStats>

}