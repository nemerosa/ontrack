package net.nemerosa.ontrack.model.dashboards.widgets

import org.springframework.stereotype.Component

@Component
class E2ELeadTimeChartWidget : AbstractWidget<E2EChartWidgetConfig>(
    key = "home/E2ELeadTimeChart",
    name = "E2E Lead time to promotion",
    description = "Chart displaying the lead time to promotion from one project to another.",
    defaultConfig = E2EChartWidgetConfig(
        project = null,
        branch = null,
        promotionLevel = null,
        targetProject = null,
        targetBranch = null,
        targetPromotionLevel = null
    ),
    preferredHeight = 30,
)
