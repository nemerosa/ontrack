package net.nemerosa.ontrack.model.dashboards

/**
 * A _dashboard_ is a _layout_ associated to a list of widgets.
 *
 * @property key ID of the dashboard
 * @property name Display name for the dashboard
 * @property layoutKey ID of the layout to use (no configuration needed)
 * @property widgets List of widgets in this dashboard
 */
data class Dashboard(
    val key: String,
    val name: String,
    val layoutKey: String,
    val widgets: List<WidgetInstance>,
)