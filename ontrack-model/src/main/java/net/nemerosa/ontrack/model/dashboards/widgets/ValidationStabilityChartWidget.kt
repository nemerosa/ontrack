package net.nemerosa.ontrack.model.dashboards.widgets

import org.springframework.stereotype.Component

@Component
class ValidationStabilityChartWidget : AbstractWidget<ValidationChartWidgetConfig>(
    key = "home/ValidationStabilityChart",
    name = "Validation stability",
    description = "Chart displaying the percentage of builds being validated.",
    defaultConfig = ValidationChartWidgetConfig(project = null, branch = null, validationStamp = null),
    preferredHeight = 30,
)
