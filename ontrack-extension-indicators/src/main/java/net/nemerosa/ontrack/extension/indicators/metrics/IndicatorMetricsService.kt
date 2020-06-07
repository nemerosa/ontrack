package net.nemerosa.ontrack.extension.indicators.metrics

import net.nemerosa.ontrack.extension.indicators.model.Indicator
import net.nemerosa.ontrack.model.structure.Project

interface IndicatorMetricsService {

    fun <T> saveMetrics(project: Project, indicator: Indicator<T>)

}