package net.nemerosa.ontrack.extension.indicators.portfolio

import net.nemerosa.ontrack.model.labels.Label
import net.nemerosa.ontrack.model.structure.Project

interface IndicatorPortfolioService {

    /**
     * Creates a new portfolio
     *
     * @param id Unique ID for the portfolio
     * @param name Display name for the portfolio
     */
    fun createPortfolio(id: String, name: String): IndicatorPortfolio

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

    /**
     * Finds a portfolio using its ID
     */
    fun findPortfolioById(id: String): IndicatorPortfolio?

    /**
     * Updates a portfolio
     */
    fun updatePortfolio(id: String, input: PortfolioUpdateForm): IndicatorPortfolio

    /**
     * Deletes a portfolio
     */
    fun deletePortfolio(id: String)

    /**
     * Gets the portfolio of portfolios
     */
    fun getPortfolioOfPortfolios(): IndicatorPortfolioOfPortfolios

    /**
     * Saves the portfolio of portfolios
     */
    fun savePortfolioOfPortfolios(input: IndicatorPortfolioOfPortfolios): IndicatorPortfolioOfPortfolios

}