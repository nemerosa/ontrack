package net.nemerosa.ontrack.extension.chart

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseInto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultChartService(
    private val chartRegistry: ChartRegistry,
) : ChartService {

    override fun getChart(input: GetChartInput): JsonNode {
        val provider = chartRegistry.getProvider<Any,Chart>(input.name)
        return getChart<Any,Chart>(provider, input)
    }

    private fun <T : Any, C: Chart> getChart(provider: ChartProvider<T, C>, input: GetChartInput): JsonNode {
        // Parsing of parameters
        val parameters = provider.parseParameters(input.parameters)
        // Getting the chart
        return provider.getChart(input.options, parameters).asJson()
    }

}