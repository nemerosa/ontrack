package net.nemerosa.ontrack.graphql.dashboards

import com.fasterxml.jackson.databind.JsonNode

data class UpdateWidgetConfigInput(
    val dashboardKey: String,
    val widgetKey: String,
    val config: JsonNode,
)
