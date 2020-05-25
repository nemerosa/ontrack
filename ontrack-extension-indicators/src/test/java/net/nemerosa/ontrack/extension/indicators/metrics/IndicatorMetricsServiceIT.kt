package net.nemerosa.ontrack.extension.indicators.metrics

import com.nhaarman.mockitokotlin2.*
import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import net.nemerosa.ontrack.job.JobRunListener
import net.nemerosa.ontrack.model.metrics.MetricsExportService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import java.time.Duration

class IndicatorMetricsServiceIT : AbstractIndicatorsTestSupport() {

    @Autowired
    private lateinit var metricsExportService: MetricsExportService

    @Autowired
    private lateinit var indicatorMetricsRestorationJob: IndicatorMetricsRestorationJob

    @Test
    fun `Setting indicators sends some metrics`() {
        val type = category().booleanType()
        project {
            indicator(type, true)
            // Checks the metrics have been sent
            verify(metricsExportService).exportMetrics(
                    metric = eq("ontrack_indicator"),
                    tags = eq(mapOf(
                            "project" to name,
                            "category" to type.category.id,
                            "type" to type.id
                    )),
                    fields = eq(mapOf(
                            "value" to 100.0
                    )),
                    timestamp = any()
            )
        }
    }

    @Test
    fun `Metrics restoration`() {
        val type = category().booleanType(required = false)
        // Generates a stream of metrics in the past
        val now = Time.now()
        project {
            (9 downTo 0).forEach { days ->
                indicator(type, (days % 2 == 0), time = now - Duration.ofDays(days.toLong()))
            }
            // Resetting the mock
            reset(metricsExportService)
            // Restoration job
            indicatorMetricsRestorationJob.startingJobs.first().job.task.run(JobRunListener.out())
            // Checks that the metrics for this project were sent back
            verify(metricsExportService, times(5)).exportMetrics(
                    metric = eq("ontrack_indicator"),
                    tags = eq(mapOf(
                            "project" to name,
                            "category" to type.category.id,
                            "type" to type.id
                    )),
                    fields = eq(mapOf(
                            "value" to 100.0
                    )),
                    timestamp = any()
            )
            verify(metricsExportService, times(5)).exportMetrics(
                    metric = eq("ontrack_indicator"),
                    tags = eq(mapOf(
                            "project" to name,
                            "category" to type.category.id,
                            "type" to type.id
                    )),
                    fields = eq(mapOf(
                            "value" to 50.0
                    )),
                    timestamp = any()
            )
        }
    }

    @Configuration
    @Profile(RunProfile.UNIT_TEST)
    class IndicatorMetricsServiceITConfig {
        @Bean
        @Primary
        fun metricsExportService() = mock<MetricsExportService>()
    }

}