package net.nemerosa.ontrack.extension.indicators.metrics

import net.nemerosa.ontrack.extension.indicators.IndicatorConfigProperties
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.model.metrics.MetricsReexportJobProvider
import net.nemerosa.ontrack.model.support.JobProvider
import net.nemerosa.ontrack.model.support.RestorationJobs
import org.springframework.stereotype.Component

@Component
class IndicatorMetricsExportJob(
    private val indicatorMetricsExportService: IndicatorMetricsExportService,
    private val indicatorConfigProperties: IndicatorConfigProperties,
) : JobProvider, Job, MetricsReexportJobProvider {

    override fun getStartingJobs(): Collection<JobRegistration> =
        listOf(
            JobRegistration(
                this,
                if (indicatorConfigProperties.metrics.enabled) {
                    // Cron
                    Schedule.cron(indicatorConfigProperties.metrics.cron)
                } else {
                    Schedule.NONE // Manually only
                }
            )
        )

    override fun isDisabled(): Boolean = false

    override fun getReexportJobKey(): JobKey = key

    override fun getKey(): JobKey =
        RestorationJobs.RESTORATION_JOB_TYPE.getKey("indicator-metrics-export")

    override fun getDescription(): String = "Indicator Metrics Export"

    override fun getTask() = JobRun { listener ->
        indicatorMetricsExportService.export {
            listener.message(it)
        }
    }

}