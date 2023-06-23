package net.nemerosa.ontrack.model.dashboards

import com.fasterxml.jackson.databind.JsonNode

/**
 * A _widget instance_ is the association of a _widget_ and its _configuration_.
 *
 * @property key
 */
data class WidgetInstance(
    val key: String,
    val config: JsonNode,
)
