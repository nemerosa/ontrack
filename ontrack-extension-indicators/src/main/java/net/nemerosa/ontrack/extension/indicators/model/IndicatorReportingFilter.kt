package net.nemerosa.ontrack.extension.indicators.model

/**
 * Filter on projects for the reporting of indicators
 */
data class IndicatorReportingFilter(
    val filledOnly: Boolean? = null,
    val projectName: String? = null,
    val projectId: Int? = null,
    val portfolio: String? = null,
    val label: String? = null,
)