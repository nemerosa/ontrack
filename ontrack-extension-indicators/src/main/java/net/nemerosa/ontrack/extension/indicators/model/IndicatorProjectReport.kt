package net.nemerosa.ontrack.extension.indicators.model

import net.nemerosa.ontrack.extension.indicators.ui.ProjectIndicator
import net.nemerosa.ontrack.model.structure.Project

class IndicatorProjectReport(
    val items: List<IndicatorProjectReportItem>
)

class IndicatorProjectReportItem(
    val project: Project,
    val indicators: List<Indicator<*>>
)
