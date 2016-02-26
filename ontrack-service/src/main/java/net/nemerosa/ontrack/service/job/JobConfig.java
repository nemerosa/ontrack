package net.nemerosa.ontrack.service.job;

import net.nemerosa.ontrack.job.JobScheduler;
import net.nemerosa.ontrack.job.support.DefaultJobScheduler;
import net.nemerosa.ontrack.model.support.OntrackConfigProperties;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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

}
