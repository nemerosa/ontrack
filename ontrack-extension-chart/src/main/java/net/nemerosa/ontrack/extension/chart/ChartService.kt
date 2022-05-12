package net.nemerosa.ontrack.extension.chart

import com.fasterxml.jackson.databind.JsonNode

interface ChartService {

    /**
     * Given a chart request, returns some chart data.
     */
    fun getChart(input: GetChartInput): JsonNode

}