package net.nemerosa.ontrack.model.dashboards.widgets

import org.springframework.stereotype.Component

@Component
class ValidationMetricsChartWidget : AbstractWidget<ValidationChartWidgetConfig>(
    key = "home/ValidationMetricsChart",
    name = "Validation metrics",
    description = "Chart displaying metrics of a validation over time.",
    defaultConfig = ValidationChartWidgetConfig(project = null, branch = null, validationStamp = null),
    preferredHeight = 30,
)
