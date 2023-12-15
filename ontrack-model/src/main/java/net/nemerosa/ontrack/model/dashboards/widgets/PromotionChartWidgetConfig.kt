package net.nemerosa.ontrack.model.dashboards.widgets

data class PromotionChartWidgetConfig(
    val project: String?,
    val branch: String?,
    val promotionLevel: String?,
    val interval: String = "3m",
    val period: String = "1w",
): WidgetConfig
