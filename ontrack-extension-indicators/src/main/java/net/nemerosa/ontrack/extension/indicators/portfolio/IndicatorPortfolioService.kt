package net.nemerosa.ontrack.extension.indicators.portfolio

import net.nemerosa.ontrack.model.labels.Label

interface IndicatorPortfolioService {

    /**
     * Creates a new portfolio
     */
    fun createPortfolio(name: String): IndicatorPortfolio

    /**
     * Gets the list of labels for this portfolio
     */
    fun getPortfolioLabels(portfolio: IndicatorPortfolio): List<Label>

    /**
     * Gets the list of all portfolios
     */
    fun findAll(): List<IndicatorPortfolio>

}