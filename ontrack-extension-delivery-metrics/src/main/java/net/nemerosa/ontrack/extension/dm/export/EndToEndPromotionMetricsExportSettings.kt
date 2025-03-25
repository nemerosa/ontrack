package net.nemerosa.ontrack.extension.dm.export

import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Settings for the export of the end-to-end promotion metrics.
 */
data class EndToEndPromotionMetricsExportSettings(
    @APIDescription("Export enabled")
    val enabled: Boolean = DEFAULT_ENABLED,
    @APIDescription("Regex for the branches eligible for the export")
    val branches: String = DEFAULT_BRANCHES,
    @APIDescription("Number of days in the past when looking for event metrics")
    val pastDays: Int = DEFAULT_PAST_DAYS,
    @APIDescription("Number of days in the past to restore")
    val restorationDays: Int = DEFAULT_RESTORATION_DAYS,
) {

    companion object {
        const val DEFAULT_ENABLED = false
        const val DEFAULT_BRANCHES = "develop|main|master|release-.*|gatekeeper|maintenance-.*"
        const val DEFAULT_PAST_DAYS = 7
        const val DEFAULT_RESTORATION_DAYS = 1460 // 4 years
    }

}
