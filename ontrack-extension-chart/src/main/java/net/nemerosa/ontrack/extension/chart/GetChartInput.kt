package net.nemerosa.ontrack.extension.chart

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.graphql.support.TypeRef
import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Input to get some chart data")
data class GetChartInput(
    @APIDescription("Name of the chart")
    val name: String,
    @APIDescription("General options for the chart")
    @TypeRef(embedded = true)
    val options: GetChartOptions,
    @APIDescription("Parameters for the chart (as JSON)")
    val parameters: JsonNode,
)
