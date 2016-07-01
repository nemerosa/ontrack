package net.nemerosa.ontrack.service.job;

import com.codahale.metrics.MetricRegistry;
import net.nemerosa.ontrack.job.JobListener;
import net.nemerosa.ontrack.job.JobScheduler;
import net.nemerosa.ontrack.job.Schedule;
import net.nemerosa.ontrack.job.orchestrator.JobOrchestrator;
import net.nemerosa.ontrack.job.orchestrator.JobOrchestratorSupplier;
import net.nemerosa.ontrack.job.support.DefaultJobScheduler;
import net.nemerosa.ontrack.model.support.ApplicationLogService;
import net.nemerosa.ontrack.model.support.OntrackConfigProperties;
import net.nemerosa.ontrack.model.support.SettingsRepository;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class JobConfig {

    @Autowired
    private OntrackConfigProperties ontrackConfigProperties;

    @Autowired
    private DefaultJobDecorator jobDecorator;

    @Autowired
    private ApplicationLogService logService;

    @Autowired
    private MetricRegistry metricRegistry;

    @Autowired
    private CounterService counterService;

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired(required = false)
    private Collection<JobOrchestratorSupplier> jobOrchestratorSuppliers;

    @Bean
    public JobOrchestrator jobOrchestrator() {
        return new JobOrchestrator(
                jobScheduler(),
                Schedule.EVERY_MINUTE, // TODO Makes this configurable in OntrackConfigProperties
                "Job orchestration",
                jobOrchestratorSuppliers
        );
    }

    @Bean
    public JobListener jobListener() {
        return new DefaultJobListener(
                logService,
                metricRegistry,
                counterService,
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
        return new DefaultJobScheduler(
                jobDecorator,
                jobExecutorService(),
                jobListener()
        );
    }

}
