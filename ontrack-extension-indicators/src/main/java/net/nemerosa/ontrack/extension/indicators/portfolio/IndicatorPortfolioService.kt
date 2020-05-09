package net.nemerosa.ontrack.extension.indicators.portfolio

interface IndicatorPortfolioService {

    /**
     * Creates a new portfolio
     */
    fun createPortfolio(name: String): IndicatorPortfolio

}