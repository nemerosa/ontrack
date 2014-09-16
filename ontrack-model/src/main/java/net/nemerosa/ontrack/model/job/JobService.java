package net.nemerosa.ontrack.model.job;

import net.nemerosa.ontrack.model.Ack;

import java.util.Collection;

/**
 * Job orchestrator.
 */
public interface JobService {

    /**
     * List of job statuses
     */
    Collection<JobStatus> getJobStatuses();

    /**
     * Tries to launch a job.
     *
     * @param category Category of the job
     * @param id       ID of the job in the category
     * @return {@link Ack#OK} if the job could actually be launched, {@link Ack#NOK} otherwise.
     */
    Ack launchJob(String category, String id);
}
