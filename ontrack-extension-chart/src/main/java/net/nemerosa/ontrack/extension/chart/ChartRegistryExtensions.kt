package net.nemerosa.ontrack.extension.chart

fun <T: Any, C: Chart> ChartRegistry.getProvider(name: String): ChartProvider<T, C> =
    findProvider(name) ?: throw ChartProviderNotFoundException(name)
