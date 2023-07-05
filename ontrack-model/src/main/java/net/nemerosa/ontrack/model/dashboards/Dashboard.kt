package net.nemerosa.ontrack.model.dashboards

import com.fasterxml.jackson.databind.JsonNode

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
) {
    fun updateWidgetConfig(widgetUuid: String, widgetConfig: JsonNode) = Dashboard(
        key = key,
        name = name,
        layoutKey = layoutKey,
        widgets = widgets.map { widget ->
            if (widget.uuid == widgetUuid) {
                widget.updateConfig(widgetConfig)
            } else {
                widget
            }
        },
    )
}