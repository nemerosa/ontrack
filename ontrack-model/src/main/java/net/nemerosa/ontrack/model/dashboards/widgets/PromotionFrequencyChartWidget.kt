package net.nemerosa.ontrack.model.dashboards.widgets

import org.springframework.stereotype.Component

@Component
class PromotionFrequencyChartWidget: AbstractWidget<PromotionChartWidgetConfig>(
    key = "home/PromotionFrequencyChart",
    name = "Promotion frequency",
    description = "Chart displaying the number of times a promotion occurs during the selected period.",
    defaultConfig = PromotionChartWidgetConfig(project = null, branch = null, promotionLevel = null),
    preferredHeight = 6,
)
