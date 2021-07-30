package net.nemerosa.ontrack.extension.indicators.portfolio

/**
 * Collection of indicator categories.
 *
 * @property id UUID (empty for a view to be created)
 * @property name Unique name for this view
 * @property categories List of indicator categories
 */
class IndicatorView(
    val id: String,
    val name: String,
    val categories: List<String>
)
