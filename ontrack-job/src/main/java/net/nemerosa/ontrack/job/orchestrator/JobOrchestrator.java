package net.nemerosa.ontrack.job.orchestrator;

import net.nemerosa.ontrack.job.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class JobOrchestrator implements Job {

    private final JobScheduler jobScheduler;
    private final String name;
    private final Collection<JobOrchestratorSupplier> jobOrchestratorSuppliers;

    private final Set<JobKey> cache = new HashSet<>();

    public JobOrchestrator(JobScheduler jobScheduler, String name, Collection<JobOrchestratorSupplier> jobOrchestratorSuppliers) {
        this.jobScheduler = jobScheduler;
        this.name = name;
        this.jobOrchestratorSuppliers = jobOrchestratorSuppliers;
    }

    @Override
    public JobKey getKey() {
        return JobCategory.CORE.getType("orchestrator").withName("Orchestrator").getKey(name);
    }

    @Override
    public JobRun getTask() {
        return this::orchestrate;
    }

    public synchronized void orchestrate(JobRunListener runListener) {
        // Complete list of registrations
        Collection<JobRegistration> registrations = jobOrchestratorSuppliers.stream()
                .flatMap(JobOrchestratorSupplier::collectJobRegistrations)
                .collect(Collectors.toList());
        // List of keys
        Set<JobKey> keys = registrations.stream()
                .map(registration -> registration.getJob().getKey())
                .collect(Collectors.toSet());
        // Jobs to unschedule
        Set<JobKey> toRemove = new HashSet<>(cache);
        toRemove.removeAll(keys);
        toRemove.forEach(jobScheduler::unschedule);
        // Jobs to add
        Set<JobKey> toAdd = new HashSet<>(keys);
        toAdd.removeAll(cache);
        registrations.stream()
                .filter(jobRegistration -> toAdd.contains(jobRegistration.getJob().getKey()))
                .forEach(jobRegistration -> schedule(jobRegistration, runListener));
        // Resets the cache
        cache.clear();
        cache.addAll(keys);
    }

    private void schedule(JobRegistration jobRegistration, JobRunListener runListener) {
        runListener.message("Scheduling: %s", jobRegistration.getJob().getKey());
        jobScheduler.schedule(jobRegistration.getJob(), jobRegistration.getSchedule());
    }

    @Override
    public String getDescription() {
        return name;
    }

    @Override
    public boolean isDisabled() {
        return false;
    }

}
