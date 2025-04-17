package net.nemerosa.ontrack.extension.indicators.metrics

import io.mockk.clearMocks
import io.mockk.mockk
import io.mockk.verify
import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import net.nemerosa.ontrack.job.JobRunListener
import net.nemerosa.ontrack.model.metrics.MetricsExportService
import org.junit.jupiter.api.Test
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
    private lateinit var indicatorMetricsRestorationJob: IndicatorMetricsExportJob

    @Test
    fun `Metrics export`() {
        val type = category().booleanType(required = false)
        // Generates a stream of metrics in the past
        val now = Time.now()
        project {
            (9 downTo 0).forEach { days ->
                indicator(type, (days % 2 == 0), time = now - Duration.ofDays(days.toLong()))
            }
            // Resetting the mock
            clearMocks(metricsExportService)
            // Restoration job
            indicatorMetricsRestorationJob.startingJobs.first().job.task.run(JobRunListener.out())
            // Checks that the metrics for this project were sent back
            verify(exactly = 5) {
                metricsExportService.exportMetrics(
                    metric = "ontrack_indicator",
                    tags = mapOf(
                        "project" to name,
                        "category" to type.category.id,
                        "type" to type.id
                    ),
                    fields = mapOf(
                        "value" to 100.0
                    ),
                    timestamp = any()
                )
            }
            verify(exactly = 5) {
                metricsExportService.exportMetrics(
                    metric = "ontrack_indicator",
                    tags = mapOf(
                        "project" to name,
                        "category" to type.category.id,
                        "type" to type.id
                    ),
                    fields = mapOf(
                        "value" to 50.0
                    ),
                    timestamp = any()
                )
            }
        }
    }

    @Configuration
    @Profile(RunProfile.DEV)
    class IndicatorMetricsServiceITConfig {
        @Bean
        @Primary
        fun metricsExportService() = mockk<MetricsExportService>(relaxed = true)
    }

}