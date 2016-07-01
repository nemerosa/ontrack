package net.nemerosa.ontrack.service.job;

import net.nemerosa.ontrack.job.JobScheduler;
import net.nemerosa.ontrack.job.Schedule;
import net.nemerosa.ontrack.job.orchestrator.JobOrchestrator;
import net.nemerosa.ontrack.job.orchestrator.JobOrchestratorSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

@Configuration
public class JobOrchestrationConfig {

    @Autowired
    private JobScheduler jobScheduler;

    @Autowired(required = false)
    private Collection<JobOrchestratorSupplier> jobOrchestratorSuppliers;

    @Bean
    public JobOrchestrator jobOrchestrator() {
        return new JobOrchestrator(
                jobScheduler,
                Schedule.EVERY_MINUTE, // TODO Makes this configurable in OntrackConfigProperties
                "Job orchestration",
                jobOrchestratorSuppliers
        );
    }

}
