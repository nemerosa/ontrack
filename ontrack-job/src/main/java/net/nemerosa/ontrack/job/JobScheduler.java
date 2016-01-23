package net.nemerosa.ontrack.job;

import java.util.concurrent.Future;

public interface JobScheduler {

    /**
     * Schedules or reschedules a job in the scheduler. If the job is already running,
     * does not stop its current execution.
     */
    void schedule(Job job, Schedule schedule);

    // TODO Gets list of job statuses

    // TODO Fires a job immediately

    /**
     * Fires a job immediately, without waiting the schedule
     */
    Future<?> fireImmediately(JobKey jobKey);

}
