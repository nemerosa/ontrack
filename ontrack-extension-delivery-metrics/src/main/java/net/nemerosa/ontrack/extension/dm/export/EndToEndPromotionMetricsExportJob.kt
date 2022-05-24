package net.nemerosa.ontrack.extension.dm.export

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.dm.model.DeliveryMetricsJobs
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.JobProvider
import org.springframework.stereotype.Component

@Component
class EndToEndPromotionMetricsExportJob(
    private val endPromotionMetricsExportService: EndToEndPromotionMetricsExportService,
    private val cachedSettingsService: CachedSettingsService,
) : JobProvider {
    override fun getStartingJobs() = listOf(
        JobRegistration(
            createJob(),
            Schedule.EVERY_HOUR,
        )
    )

    private fun createJob() = object : Job {

        override fun getKey(): JobKey =
            DeliveryMetricsJobs.DM_JOB_CATEGORY
                .getType("end-to-end-promotion-metrics")
                .withName("End to end promotion metrics")
                .getKey("export")

        override fun getTask() = JobRun {
            val settings = cachedSettingsService.getCachedSettings(EndToEndPromotionMetricsExportSettings::class.java)
            if (settings.enabled) {
                val now = Time.now()
                endPromotionMetricsExportService.exportMetrics(
                    settings.branches,
                    now.minusDays(settings.pastDays.toLong()),
                    now,
                )
            }
        }

        override fun getDescription(): String =
            "Export of end-to-end promotion metrics"

        override fun isDisabled(): Boolean =
            !cachedSettingsService.getCachedSettings(EndToEndPromotionMetricsExportSettings::class.java).enabled

    }
}