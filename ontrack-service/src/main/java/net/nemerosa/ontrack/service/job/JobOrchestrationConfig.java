package net.nemerosa.ontrack.service.job;

import net.nemerosa.ontrack.job.JobRegistration;
import net.nemerosa.ontrack.job.JobScheduler;
import net.nemerosa.ontrack.job.Schedule;
import net.nemerosa.ontrack.job.orchestrator.JobOrchestrator;
import net.nemerosa.ontrack.job.orchestrator.JobOrchestratorSupplier;
import net.nemerosa.ontrack.model.support.JobProvider;
import net.nemerosa.ontrack.model.support.OntrackConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Collections;

@Configuration
public class JobOrchestrationConfig {

    @Autowired
    private OntrackConfigProperties configProperties;

    @Autowired
    private JobScheduler jobScheduler;

    @Autowired(required = false)
    private Collection<JobOrchestratorSupplier> jobOrchestratorSuppliers;

    @Bean
    public JobOrchestrator jobOrchestrator() {
        return new JobOrchestrator(
                jobScheduler,
                "Collection of jobs",
                jobOrchestratorSuppliers
        );
    }

    @Bean
    public JobProvider jobOrchestratorRegistration() {
        return () -> Collections.singleton(
                new JobRegistration(
                        jobOrchestrator(),
                        Schedule.everyMinutes(configProperties.getJobs().getOrchestration())
                )
        );
    }

}
