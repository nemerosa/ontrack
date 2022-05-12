package net.nemerosa.ontrack.extension.chart

fun <T: Any> ChartRegistry.getProvider(name: String): ChartProvider<T> =
    findProvider(name) ?: throw ChartProviderNotFoundException(name)
