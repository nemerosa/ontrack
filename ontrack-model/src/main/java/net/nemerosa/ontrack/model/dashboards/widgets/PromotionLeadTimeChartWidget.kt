package net.nemerosa.ontrack.model.dashboards.widgets

import org.springframework.stereotype.Component

@Component
class PromotionLeadTimeChartWidget: AbstractWidget<PromotionChartWidgetConfig>(
    key = "home/PromotionLeadTimeChart",
    name = "Lead time to promotion",
    description = "Chart displaying the lead time to promotion.",
    defaultConfig = PromotionChartWidgetConfig(project = null, branch = null, promotionLevel = null),
    preferredHeight = 30,
)
