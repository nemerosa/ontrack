package net.nemerosa.ontrack.model.dashboards.widgets

data class E2EChartWidgetConfig(
    val project: String?,
    val branch: String?,
    val promotionLevel: String?,
    val targetProject: String?,
    val targetBranch: String?,
    val targetPromotionLevel: String?,
    val interval: String = "3m",
    val period: String = "1w",
): WidgetConfig
