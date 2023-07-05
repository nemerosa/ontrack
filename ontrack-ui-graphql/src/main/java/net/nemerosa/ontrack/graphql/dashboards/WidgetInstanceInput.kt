package net.nemerosa.ontrack.graphql.dashboards

import com.fasterxml.jackson.databind.JsonNode

data class WidgetInstanceInput(
    val uuid: String?,
    val key: String,
    val config: JsonNode,
)
