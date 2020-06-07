package net.nemerosa.ontrack.extension.indicators.metrics

import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.model.support.JobProvider
import net.nemerosa.ontrack.model.support.RestorationJobs
import org.springframework.stereotype.Component

@Component
class IndicatorMetricsRestorationJob(
        private val indicatorMetricsRestorationService: IndicatorMetricsRestorationService
) : JobProvider, Job {

    override fun getStartingJobs(): Collection<JobRegistration> =
            listOf(
                    JobRegistration(
                            this,
                            Schedule.NONE // Manually only
                    )
            )

    override fun isDisabled(): Boolean = false

    override fun getKey(): JobKey =
            RestorationJobs.RESTORATION_JOB_TYPE.getKey("indicator-metrics-restoration")

    override fun getDescription(): String = "Indicator Metrics Restoration"

    override fun getTask() = JobRun { listener ->
        indicatorMetricsRestorationService.restore {
            listener.message(it)
        }
    }

}