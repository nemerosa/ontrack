package net.nemerosa.ontrack.model.job;

import java.util.Collection;

/**
 * Job orchestrator.
 */
public interface JobService {

    /**
     * List of running jobs
     */
    Collection<JobDescriptor> getRunningJobs();

    /**
     * List of registered jobs
     */
    Collection<JobDescriptor> getRegisteredJobs();

}
