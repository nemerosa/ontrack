package net.nemerosa.ontrack.model.job;

import java.util.Collection;

/**
 * Job orchestrator.
 */
public interface JobService {

    /**
     * List of job statuses
     */
    Collection<JobStatus> getJobStatuses();
    
}
