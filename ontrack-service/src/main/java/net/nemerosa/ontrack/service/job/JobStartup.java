package net.nemerosa.ontrack.service.job;

import net.nemerosa.ontrack.job.JobScheduler;
import net.nemerosa.ontrack.model.support.JobProvider;
import net.nemerosa.ontrack.model.support.StartupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Starting the jobs at startup.
 */
@Component
public class JobStartup implements StartupService {

    private final JobScheduler jobScheduler;
    private final Collection<JobProvider> jobProviders;

    @Autowired
    public JobStartup(JobScheduler jobScheduler, Collection<JobProvider> jobProviders) {
        this.jobScheduler = jobScheduler;
        this.jobProviders = jobProviders;
    }

    @Override
    public String getName() {
        return "Job registration at startup";
    }

    @Override
    public int startupOrder() {
        return StartupService.JOB_REGISTRATION;
    }

    @Override
    public void start() {
        jobProviders.stream()
                .flatMap(jobProvider -> jobProvider.getStartingJobs().stream())
                .forEach(jobRegistration -> jobScheduler.schedule(
                        jobRegistration.getJob(),
                        jobRegistration.getSchedule()
                ));
    }
}
