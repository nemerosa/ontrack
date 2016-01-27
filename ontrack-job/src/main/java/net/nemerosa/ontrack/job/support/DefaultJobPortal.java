package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.job.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class DefaultJobPortal implements JobPortal, Job {

    private static final JobCategory SYSTEM_CATEGORY = JobCategory.of("system").withName("System");

    private static final JobType PORTAL_JOB = SYSTEM_CATEGORY.getType("portal").withName("Job portal");


    private final Logger logger = LoggerFactory.getLogger(JobPortal.class);

    private final JobScheduler jobScheduler;

    private final Collection<JobDefinitionProvider> jobProviders = new ArrayList<>();

    public DefaultJobPortal(JobScheduler jobScheduler, Schedule schedule) {
        this.jobScheduler = jobScheduler;
        this.jobScheduler.schedule(this, schedule);
    }

    @Override
    public void registerJobProvider(JobDefinitionProvider jobProvider) {
        logger.info("[job][system][job-portal] Registering {}", jobProvider);
        jobProviders.add(jobProvider);
    }

    @Override
    public JobScheduler getJobScheduler() {
        return jobScheduler;
    }

    @Override
    public Future<?> fire() {
        return jobScheduler.fireImmediately(getKey());
    }

    @Override
    public JobKey getKey() {
        return PORTAL_JOB.getKey("job-registration");
    }

    @Override
    public JobRun getTask() {
        return (listener) -> {
            logger.debug("[job][system][job-portal] Running the registration for {} providers", jobProviders.size());
            jobProviders.forEach(this::register);
        };
    }

    protected void register(JobDefinitionProvider jobProvider) {
        Collection<JobDefinition> jobDefinitions = jobProvider.getJobs();
        logger.debug("[job][system][job-portal] Running the registration for {}: {} job(s)", jobProvider, jobDefinitions.size());
        // Checks type consistency
        jobDefinitions.forEach(jobDefinition -> checkJobType(jobProvider, jobDefinition));
        // Get all keys
        Set<JobKey> jobKeys = jobDefinitions.stream().map(definition -> definition.getJob().getKey()).collect(Collectors.toSet());
        // Get keys of jobs being already registered
        Collection<JobKey> scheduledKeys = jobScheduler.getJobKeysOfCategory(jobProvider.getJobCategory());
        // Gets the keys of the jobs not being scheduled yet
        Set<JobKey> jobToUnschedule = new HashSet<>(scheduledKeys);
        jobToUnschedule.removeAll(jobKeys);
        // Removes the unscheduled jobs
        jobToUnschedule.forEach(this::unschedule);
        // Reschedule jobs (only when different schedule)
        jobDefinitions.forEach(this::reschedule);
    }

    private void unschedule(JobKey key) {
        logger.debug("[job][system][job-portal] Unscheduling [{}][{}]", key.getType(), key.getId());
        jobScheduler.unschedule(key);
    }

    protected void checkJobType(JobDefinitionProvider jobProvider, JobDefinition jobDefinition) {
        if (!jobDefinition.getJob().getKey().sameCategory(jobProvider.getJobCategory())) {
            throw new JobProviderTypeInconsistencyException(
                    jobProvider.getClass(),
                    jobProvider.getJobCategory(),
                    jobDefinition.getJob().getKey()
            );
        }
    }

    protected void reschedule(JobDefinition jobDefinition) {
        JobKey key = jobDefinition.getJob().getKey();
        Optional<JobStatus> jobStatus = jobScheduler.getJobStatus(key);
        if (jobStatus.isPresent() && jobStatus.get().getSchedule().sameDelayThan(jobDefinition.getSchedule())) {
            logger.debug("[job][system][job-portal] Not rescheduling [{}][{}] - same schedule", key.getType(), key.getId());
        } else {
            logger.debug("[job][system][job-portal] Rescheduling [{}][{}] with schedule {}", key.getType(), key.getId(), jobDefinition.getSchedule());
            jobScheduler.schedule(jobDefinition.getJob(), jobDefinition.getSchedule());
        }
    }

    @Override
    public String getDescription() {
        return "Orchestration of jobs with the scheduler";
    }

    @Override
    public boolean isDisabled() {
        return false;
    }
}
