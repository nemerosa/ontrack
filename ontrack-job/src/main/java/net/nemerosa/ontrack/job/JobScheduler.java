package net.nemerosa.ontrack.job;

import net.nemerosa.ontrack.job.support.JobNotScheduledException;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.Future;

public interface JobScheduler {

    /**
     * Schedules or reschedules a job in the scheduler. If the job is already running,
     * does not stop its current execution.
     *
     * @param job      Job to schedule
     * @param schedule Schedule for the job
     */
    void schedule(Job job, Schedule schedule);

    /**
     * Removes a job from the scheduler. Any running execution will be stopped immediately.
     *
     * @param key Key of the job to unschedule
     * @return True if the job was running and had to be stopped
     */
    boolean unschedule(JobKey key);

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
     *
     * @param key Key of the job to pause
     * @return <code>true</code> if the job was paused
     * @throws JobNotScheduledException If the job is not scheduled
     */
    boolean pause(JobKey key);

    /**
     * Resumes the execution of a job
     *
     * @param key Key of the job to resume
     * @return <code>true</code> if the job was resumed
     * @throws JobNotScheduledException If the job is not scheduled
     */
    boolean resume(JobKey key);

    /**
     * Gets the status for a job
     *
     * @param key Key of the job to get a status for
     * @return The job status or empty if not found.
     */
    Optional<JobStatus> getJobStatus(JobKey key);

    /**
     * Gets all the job keys
     *
     * @return List of all job keys, never null
     */
    Collection<JobKey> getAllJobKeys();

    /**
     * Gets all the job keys for a type of job
     *
     * @param type Job type
     * @return List of job keys for this type
     */
    @SuppressWarnings("unused")
    Collection<JobKey> getJobKeysOfType(JobType type);

    /**
     * Gets all the job keys for a category of jobs
     *
     * @param category Job category
     * @return List of job keys for this category
     */
    Collection<JobKey> getJobKeysOfCategory(JobCategory category);

    /**
     * Gets the list of job statuses
     *
     * @return All job statuses (never null)
     */
    Collection<JobStatus> getJobStatuses();

    /**
     * Fires a job immediately, without waiting the schedule
     *
     * @param jobKey Key of the job to fire immediately
     * @return Future for the job execution
     * @throws JobNotScheduledException If the job is not scheduled
     */
    Future<?> fireImmediately(JobKey jobKey);

    /**
     * Gets the job key for a job id
     */
    Optional<JobKey> getJobKey(long id);

    /**
     * Stops a running a job.
     *
     * @param key Key of the job to run
     * @return <code>true</code> if the job was stopped, <code>false</code> if the job could not be stopped or was
     * not running.
     * @throws JobNotScheduledException If the job is not scheduled
     */
    boolean stop(JobKey key);

    /**
     * Checks the general status of the scheduler.
     *
     * @return <code>true</code> if the scheduler is paused.
     */
    boolean isPaused();
}
