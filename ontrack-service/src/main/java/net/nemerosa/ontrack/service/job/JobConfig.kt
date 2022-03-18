package net.nemerosa.ontrack.service.job

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.job.JobListener
import net.nemerosa.ontrack.job.JobScheduler
import net.nemerosa.ontrack.job.support.DefaultJobScheduler
import net.nemerosa.ontrack.job.support.TaskExecutor
import net.nemerosa.ontrack.model.support.ApplicationLogService
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.apache.commons.lang3.concurrent.BasicThreadFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.support.CronTrigger
import java.time.Duration
import java.time.ZoneOffset
import java.util.concurrent.ScheduledFuture

@Configuration
class JobConfig(
    private val ontrackConfigProperties: OntrackConfigProperties,
    private val jobDecorator: DefaultJobDecorator,
    private val logService: ApplicationLogService,
    private val meterRegistry: MeterRegistry,
    private val settingsRepository: SettingsRepository,
) {
    @Bean
    fun jobListener(): JobListener = DefaultJobListener(
        logService,
        meterRegistry,
        settingsRepository
    )

    private fun jobTaskScheduler(): TaskScheduler = ThreadPoolTaskScheduler().apply {
        poolSize = ontrackConfigProperties.jobs.poolSize
        setThreadFactory(
            BasicThreadFactory.Builder()
                .daemon(true)
                .namingPattern("job-%s")
                .build()
        )
    }

    @Bean
    fun jobScheduler(): JobScheduler {
        val jobConfigProperties = ontrackConfigProperties.jobs
        val jobTaskScheduler = jobTaskScheduler()
        val taskExecutor = object : TaskExecutor {
            override fun scheduleAtFixedDelay(
                task: Runnable,
                initialDelay: Duration,
                delay: Duration,
            ): ScheduledFuture<*> =
                jobTaskScheduler.scheduleWithFixedDelay(
                    task,
                    Time.now().plus(initialDelay).toInstant(ZoneOffset.UTC),
                    delay
                )

            override fun scheduleCron(task: Runnable, cron: String): ScheduledFuture<*>? =
                jobTaskScheduler.schedule(task, CronTrigger(cron))
        }
        return DefaultJobScheduler(
            jobDecorator = jobDecorator,
            scheduler = taskExecutor,
            jobExecutorService = (jobTaskScheduler as ThreadPoolTaskScheduler).scheduledExecutor,
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