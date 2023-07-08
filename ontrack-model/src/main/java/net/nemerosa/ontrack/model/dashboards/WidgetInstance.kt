package net.nemerosa.ontrack.model.dashboards

import com.fasterxml.jackson.databind.JsonNode

/**
 * A _widget instance_ is the association of a _widget_ and its _configuration_.
 *
 * @property uuid Unique ID of this widget instance (since several widgets of the same type/key)
 * can be present in a dashboard.
 * @property key Identifier of the widget _type_
 * @property config Configuration for this widget inside the dashboard.
 */
data class WidgetInstance(
    val uuid: String,
    val key: String,
    val config: JsonNode,
)
