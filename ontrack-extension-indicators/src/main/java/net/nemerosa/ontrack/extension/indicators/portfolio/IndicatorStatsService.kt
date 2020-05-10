package net.nemerosa.ontrack.extension.indicators.portfolio

interface IndicatorStatsService {

    /**
     * Gets global stats for a portfolio
     */
    fun getGlobalStats(portfolio: IndicatorPortfolio): List<IndicatorTypeStats>

}