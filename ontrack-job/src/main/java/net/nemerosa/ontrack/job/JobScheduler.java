package net.nemerosa.ontrack.job;

import java.util.Collection;
import java.util.concurrent.Future;

public interface JobScheduler {

    /**
     * Schedules or reschedules a job in the scheduler. If the job is already running,
     * does not stop its current execution.
     */
    void schedule(Job job, Schedule schedule);

    /**
     * Gets the status for a job
     */
    JobStatus getJobStatus(JobKey key);

    /**
     * Gets the list of job statuses
     */
    Collection<JobStatus> getJobStatuses();

    /**
     * Fires a job immediately, without waiting the schedule
     */
    Future<?> fireImmediately(JobKey jobKey);

}
