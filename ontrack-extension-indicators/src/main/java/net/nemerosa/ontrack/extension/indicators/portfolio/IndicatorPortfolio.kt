package net.nemerosa.ontrack.extension.indicators.portfolio

/**
 * Grouping indicators for a group of projects identified by labels.
 *
 * @property id ID of the portfolio
 * @property name Name of the portfolio
 * @property labels List of labels used to identify the projects belonging to this portfolio
 * @property types List of indicator types to show for this portfolio
 */
class IndicatorPortfolio(
        val id: String,
        val name: String,
        val labels: List<String>,
        val types: List<String>
)