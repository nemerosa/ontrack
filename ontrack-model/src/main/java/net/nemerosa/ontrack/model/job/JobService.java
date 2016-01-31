package net.nemerosa.ontrack.model.job;

import net.nemerosa.ontrack.model.Ack;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.Future;

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
     * @param id ID of the job
     * @return An optional future indicating the progress of the job. If the job could not be launched for any reason,
     * no future is returned (empty).
     */
    Optional<Future<?>> launchJob(long id);
}
