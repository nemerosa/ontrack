package net.nemerosa.ontrack.extension.indicators.portfolio

import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategory
import net.nemerosa.ontrack.model.structure.Project

interface IndicatorStatsService {

    /**
     * Gets global stats for a portfolio
     */
    fun getGlobalStats(portfolio: IndicatorPortfolio): List<IndicatorCategoryStats>

    /**
     * Gets stats for a category and a project
     */
    fun getStatsForCategoryAndProject(category: IndicatorCategory, project: Project): IndicatorCategoryStats

    /**
     * Gets stats for a category and a portfolio
     */
    fun getStatsPortfolio(portfolio: IndicatorPortfolio): List<IndicatorCategoryStats>

}