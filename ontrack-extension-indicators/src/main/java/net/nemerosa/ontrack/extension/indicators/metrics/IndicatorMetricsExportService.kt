package net.nemerosa.ontrack.extension.indicators.metrics

interface IndicatorMetricsExportService {

    fun export(logger: (String) -> Unit)

}