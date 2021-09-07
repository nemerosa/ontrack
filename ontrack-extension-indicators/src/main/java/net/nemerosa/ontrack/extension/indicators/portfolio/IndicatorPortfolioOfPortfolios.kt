package net.nemerosa.ontrack.extension.indicators.portfolio

/**
 * Aggregation of all portfolios.
 *
 * @property categories List of indicator categories to show for all portfolios
 */
class IndicatorPortfolioOfPortfolios(
        @Deprecated("Use indicator view instead. Will be removed in V5.")
        val categories: List<String>
)