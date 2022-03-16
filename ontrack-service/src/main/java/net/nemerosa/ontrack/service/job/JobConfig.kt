package net.nemerosa.ontrack.service.job

import org.springframework.beans.factory.annotation.Autowired
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.model.support.ApplicationLogService
import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.job.JobListener
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.Executors
import org.apache.commons.lang3.concurrent.BasicThreadFactory
import net.nemerosa.ontrack.job.JobScheduler
import net.nemerosa.ontrack.job.support.DefaultJobScheduler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JobConfig(
    private val ontrackConfigProperties: OntrackConfigProperties,
    private val jobDecorator: DefaultJobDecorator,
    private val logService: ApplicationLogService,
    private val meterRegistry: MeterRegistry,
    private val settingsRepository: SettingsRepository
) {
    @Bean
    fun jobListener(): JobListener = DefaultJobListener(
        logService,
        meterRegistry,
        settingsRepository
    )

    @Bean
    fun jobExecutorService(): ScheduledExecutorService = Executors.newScheduledThreadPool(
        ontrackConfigProperties.jobs.poolSize,
        BasicThreadFactory.Builder()
            .daemon(true)
            .namingPattern("job-%s")
            .build()
    )

    @Bean
    fun jobScheduler(): JobScheduler {
        val jobConfigProperties = ontrackConfigProperties.jobs
        return DefaultJobScheduler(
            jobDecorator = jobDecorator,
            schedulerPool = jobExecutorService(),
            jobListener = jobListener(),
            initiallyPaused = jobConfigProperties.pausedAtStartup,
            scattering = jobConfigProperties.scattering,
            scatteringRatio = jobConfigProperties.scatteringRatio,
            meterRegistry = meterRegistry,
            timeout = jobConfigProperties.timeout,
            timeoutControllerInterval = jobConfigProperties.timeoutControllerInterval,
        )
    }
}