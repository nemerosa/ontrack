package net.nemerosa.ontrack.service.job;

import io.micrometer.core.instrument.MeterRegistry;
import net.nemerosa.ontrack.job.JobListener;
import net.nemerosa.ontrack.job.JobScheduler;
import net.nemerosa.ontrack.job.support.DefaultJobScheduler;
import net.nemerosa.ontrack.model.support.ApplicationLogService;
import net.nemerosa.ontrack.model.support.JobConfigProperties;
import net.nemerosa.ontrack.model.support.OntrackConfigProperties;
import net.nemerosa.ontrack.model.support.SettingsRepository;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class JobConfig {

    private final OntrackConfigProperties ontrackConfigProperties;

    private final DefaultJobDecorator jobDecorator;

    private final ApplicationLogService logService;

    private final MeterRegistry meterRegistry;

    private final SettingsRepository settingsRepository;

    @Autowired
    public JobConfig(OntrackConfigProperties ontrackConfigProperties, DefaultJobDecorator jobDecorator, ApplicationLogService logService, MeterRegistry meterRegistry, SettingsRepository settingsRepository) {
        this.ontrackConfigProperties = ontrackConfigProperties;
        this.jobDecorator = jobDecorator;
        this.logService = logService;
        this.meterRegistry = meterRegistry;
        this.settingsRepository = settingsRepository;
    }

    @Bean
    public JobListener jobListener() {
        return new DefaultJobListener(
                logService,
                meterRegistry,
                settingsRepository
        );
    }

    @Bean
    public ScheduledExecutorService jobExecutorService() {
        return Executors.newScheduledThreadPool(
                ontrackConfigProperties.getJobs().getPoolSize(),
                new BasicThreadFactory.Builder()
                        .daemon(true)
                        .namingPattern("job-%s")
                        .build()
        );
    }

    @Bean
    public JobScheduler jobScheduler() {
        JobConfigProperties jobConfigProperties = ontrackConfigProperties.getJobs();
        return new DefaultJobScheduler(
                jobDecorator,
                jobExecutorService(),
                jobListener(),
                jobConfigProperties.isPausedAtStartup(),
                jobConfigProperties.isScattering(),
                jobConfigProperties.getScatteringRatio(),
                meterRegistry
        );
    }

}
