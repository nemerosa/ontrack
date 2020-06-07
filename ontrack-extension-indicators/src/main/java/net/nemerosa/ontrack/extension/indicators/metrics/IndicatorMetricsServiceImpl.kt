package net.nemerosa.ontrack.extension.indicators.metrics

import net.nemerosa.ontrack.extension.indicators.model.Indicator
import net.nemerosa.ontrack.model.metrics.MetricsExportService
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class IndicatorMetricsServiceImpl(
        private val metricsExportService: MetricsExportService
) : IndicatorMetricsService {

    override fun <T> saveMetrics(project: Project, indicator: Indicator<T>) {
        if (indicator.compliance != null) {
            metricsExportService.exportMetrics(
                    IndicatorMetrics.METRIC_ONTRACK_INDICATOR,
                    tags = mapOf(
                            "project" to project.name,
                            "category" to indicator.type.category.id,
                            "type" to indicator.type.id
                    ),
                    fields = mapOf(
                            "value" to indicator.compliance.value.toDouble()
                    ),
                    timestamp = indicator.signature.time
            )
        }
    }
}