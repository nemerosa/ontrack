package net.nemerosa.ontrack.kdsl.spec.charts

import com.apollographql.apollo.api.Input
import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.kdsl.connector.graphql.GraphQLMissingDataException
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.GetChartQuery
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector
import net.nemerosa.ontrack.kdsl.spec.Ontrack
import java.time.LocalDateTime

/**
 * Getting a chart.
 */
fun Ontrack.getChart(
    name: String,
    ref: LocalDateTime? = null,
    interval: String,
    period: String,
    parameters: JsonNode,
): JsonNode =
    graphqlConnector.query(
        GetChartQuery(
            name,
            Input.optional(ref),
            interval,
            period,
            parameters
        )
    )?.chart ?: throw GraphQLMissingDataException("Did not get back the chart")
