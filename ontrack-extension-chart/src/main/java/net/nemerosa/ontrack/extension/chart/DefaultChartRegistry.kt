package net.nemerosa.ontrack.extension.chart

import org.springframework.stereotype.Component

@Component
class DefaultChartRegistry(
    providers: List<ChartProvider<*>>,
) : ChartRegistry {

    private val index = providers.associateBy { it.name }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> findProvider(name: String): ChartProvider<T>? =
        index[name] as ChartProvider<T>?
}