package net.nemerosa.ontrack.kdsl.spec.charts

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.kdsl.connector.graphql.GraphQLMissingDataException
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.GetChartQuery
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector
import net.nemerosa.ontrack.kdsl.spec.Ontrack

/**
 * Getting a chart.
 */
fun Ontrack.getChart(
    name: String,
    interval: String,
    period: String,
    parameters: JsonNode,
): JsonNode =
    graphqlConnector.query(
        GetChartQuery(
            name,
            interval,
            period,
            parameters
        )
    )?.chart ?: throw GraphQLMissingDataException("Did not get back the chart")
