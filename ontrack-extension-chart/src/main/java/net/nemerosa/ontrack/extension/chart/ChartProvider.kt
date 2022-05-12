package net.nemerosa.ontrack.extension.chart

import com.fasterxml.jackson.databind.JsonNode

/**
 * Provides a chart
 *
 * @param T Type for the parameters
 */
interface ChartProvider<T: Any> {

    /**
     * Unique name for this chart
     */
    val name: String

    /**
     * Parsing of the parameters
     */
    fun parseParameters(data: JsonNode): T

    /**
     * Gets the chart for a given input
     */
    fun getChart(options: GetChartOptions, parameters: T): JsonNode

}