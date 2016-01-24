package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.job.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultJobPortal implements JobPortal, Job {

    private final JobScheduler jobScheduler;
    private final AtomicBoolean disabled = new AtomicBoolean(false);

    private final Collection<JobProvider> jobProviders = new ArrayList<>();

    public DefaultJobPortal(JobScheduler jobScheduler) {
        this.jobScheduler = jobScheduler;
    }

    @Override
    public void registerJobProvider(JobProvider jobProvider) {
        jobProviders.add(jobProvider);
    }

    @Override
    public JobKey getKey() {
        return new JobKey("job", "portal");
    }

    @Override
    public Runnable getTask() {
        // FIXME Method net.nemerosa.ontrack.job.support.DefaultJobPortal.getTask
        return null;
    }

    @Override
    public String getDescription() {
        return "Orchestration of jobs with the scheduler";
    }

    @Override
    public boolean isDisabled() {
        return disabled.get();
    }
}
