package net.nemerosa.ontrack.extension.indicators.metrics

interface IndicatorMetricsRestorationService {

    fun restore(logger: (String) -> Unit)

}