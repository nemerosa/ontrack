package net.nemerosa.ontrack.extension.indicators.stats

import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategory
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorCategoryStats
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorPortfolio
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorView
import net.nemerosa.ontrack.model.structure.Project
import java.time.Duration

interface IndicatorStatsService {

    /**
     * Gets global stats for a portfolio
     */
    @Deprecated("Use indicator views")
    fun getGlobalStats(portfolio: IndicatorPortfolio, previous: Duration? = null): List<IndicatorCategoryStats>

    /**
     * Gets the portfolio stats for a given indicator view.
     *
     * @param portfolio Portfolio to get starts about
     * @param indicatorView View used to collect stats
     * @param previous Optional duration to compute a trend
     * @return List of stats for this portfolio and view, with optional trend indicator
     */
    fun getPortfolioViewStats(
        portfolio: IndicatorPortfolio,
        indicatorView: IndicatorView,
        previous: Duration? = null
    ): List<IndicatorCategoryStats>

    /**
     * Gets stats for a category and a project
     */
    fun getStatsForCategoryAndProject(
        category: IndicatorCategory,
        project: Project,
        previous: Duration? = null
    ): IndicatorCategoryStats

    /**
     * Gets stats for a category and a portfolio
     */
    fun getStatsPortfolio(portfolio: IndicatorPortfolio, previous: Duration? = null): List<IndicatorCategoryStats>

}