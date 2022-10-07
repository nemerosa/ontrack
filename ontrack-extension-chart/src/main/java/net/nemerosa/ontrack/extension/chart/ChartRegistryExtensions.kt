package net.nemerosa.ontrack.extension.chart

fun <S: Any, T: Any, C: Chart> ChartRegistry.getProvider(name: String): ChartProvider<S, T, C> =
    findProvider(name) ?: throw ChartProviderNotFoundException(name)
