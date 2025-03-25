package net.nemerosa.ontrack.model.dashboards.widgets

import org.springframework.stereotype.Component

@Component
class PromotionStabilityChartWidget: AbstractWidget<PromotionChartWidgetConfig>(
    key = "home/PromotionStabilityChart",
    name = "Promotion stability",
    description = "Chart displaying the percentage of builds being promoted.",
    defaultConfig = PromotionChartWidgetConfig(project = null, branch = null, promotionLevel = null),
    preferredHeight = 30,
)
