package net.nemerosa.ontrack.extension.chart

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.json.parseInto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultChartService(
    private val chartRegistry: ChartRegistry,
) : ChartService {

    override fun getChart(input: GetChartInput): JsonNode {
        val provider = chartRegistry.getProvider<Any>(input.name)
        return getChart<Any>(provider, input)
    }

    private fun <T : Any> getChart(provider: ChartProvider<T>, input: GetChartInput): JsonNode {
        // Parsing of parameters
        val parameters = provider.parseParameters(input.parameters)
        // Getting the chart
        return provider.getChart(input.options, parameters)
    }

}