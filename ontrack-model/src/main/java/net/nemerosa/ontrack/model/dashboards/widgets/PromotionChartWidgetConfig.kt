package net.nemerosa.ontrack.model.dashboards.widgets

data class PromotionChartWidgetConfig(
    val project: String?,
    val branch: String?,
    val promotionLevel: String?,
): WidgetConfig
