package net.nemerosa.ontrack.extension.indicators.portfolio

import net.nemerosa.ontrack.model.labels.Label
import net.nemerosa.ontrack.model.structure.Project

interface IndicatorPortfolioService {

    /**
     * Creates a new portfolio
     */
    fun createPortfolio(name: String): IndicatorPortfolio

    /**
     * Gets the label for this portfolio
     */
    fun getPortfolioLabel(portfolio: IndicatorPortfolio): Label?

    /**
     * Gets the list of all portfolios
     */
    fun findAll(): List<IndicatorPortfolio>

    /**
     * Gets the list of projects for this portfolio
     */
    fun getPortfolioProjects(portfolio: IndicatorPortfolio): List<Project>

}