package net.nemerosa.ontrack.service.job;

import net.nemerosa.ontrack.job.JobPortal;
import net.nemerosa.ontrack.job.JobScheduler;
import net.nemerosa.ontrack.job.Schedule;
import net.nemerosa.ontrack.job.support.DefaultJobPortal;
import net.nemerosa.ontrack.job.support.DefaultJobScheduler;
import net.nemerosa.ontrack.model.support.OntrackConfigProperties;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
public class JobConfig {

    @Autowired
    private OntrackConfigProperties ontrackConfigProperties;

    @Autowired
    private DefaultJobDecorator jobDecorator;

    @Autowired
    private DefaultJobListener jobListener;

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
                jobListener
        );
    }

    @Bean
    public JobPortal jobPortal() {
        return new DefaultJobPortal(
                jobScheduler(),
                new Schedule(
                        0,
                        ontrackConfigProperties.getJobs().getRefresh(),
                        TimeUnit.MINUTES
                )
        );
    }

}
