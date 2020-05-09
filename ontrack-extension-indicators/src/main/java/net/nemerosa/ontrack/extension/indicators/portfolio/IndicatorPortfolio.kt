package net.nemerosa.ontrack.extension.indicators.portfolio

/**
 * Grouping indicators for a group of projects identified by labels.
 *
 * @property id ID of the portfolio
 * @property name Name of the portfolio
 * @property label Label ID. If set to `null`, the portfolio is not ready to use
 * @property types List of indicator types to show for this portfolio
 */
class IndicatorPortfolio(
        val id: String,
        val name: String,
        val label: Int?,
        val types: List<String>
)