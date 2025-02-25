package net.nemerosa.ontrack.model.dashboards.widgets

import org.springframework.stereotype.Component

@Component
class PromotionTTRChartWidget : AbstractWidget<PromotionChartWidgetConfig>(
    key = "home/PromotionTTRChart",
    name = "Time to recovery to promotion",
    description = "Chart displaying the time to recovery to promotion.",
    defaultConfig = PromotionChartWidgetConfig(project = null, branch = null, promotionLevel = null),
    preferredHeight = 30,
)
