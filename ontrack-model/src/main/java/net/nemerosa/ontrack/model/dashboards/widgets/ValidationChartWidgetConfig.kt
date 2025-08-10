package net.nemerosa.ontrack.model.dashboards.widgets

data class ValidationChartWidgetConfig(
    val project: String?,
    val branch: String?,
    val validationStamp: String?,
    val interval: String = "3m",
    val period: String = "1w",
): WidgetConfig
