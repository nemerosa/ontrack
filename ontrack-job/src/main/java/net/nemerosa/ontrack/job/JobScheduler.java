package net.nemerosa.ontrack.job;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.Future;

public interface JobScheduler {

    /**
     * Schedules or reschedules a job in the scheduler. If the job is already running,
     * does not stop its current execution.
     */
    void schedule(Job job, Schedule schedule);

    /**
     * Removes a job from the scheduler. Any running execution will be stopped immediately.
     */
    void unschedule(JobKey key);

    /**
     * Pauses the scheduler
     */
    void pause();

    /**
     * Resumes the scheduler
     */
    void resume();

    /**
     * Pauses the execution of a job
     */
    void pause(JobKey key);

    /**
     * Resumes the execution of a job
     */
    void resume(JobKey key);

    /**
     * Gets the status for a job
     */
    Optional<JobStatus> getJobStatus(JobKey key);

    /**
     * Gets all the job keys
     */
    Collection<JobKey> getAllJobKeys();

    /**
     * Gets all the job keys for a type of job
     *
     * @see JobKey#getType()
     */
    Collection<JobKey> getJobKeysOfType(JobType type);

    /**
     * Gets all the job keys for a category of jobs
     *
     * @see JobType#getCategory()
     */
    Collection<JobKey> getJobKeysOfCategory(JobCategory category);

    /**
     * Gets the list of job statuses
     */
    Collection<JobStatus> getJobStatuses();

    /**
     * Fires a job immediately, without waiting the schedule
     */
    Future<?> fireImmediately(JobKey jobKey);

}
