package net.nemerosa.ontrack.model.support;

import net.nemerosa.ontrack.job.JobRegistration;

import java.util.Collection;

/**
 * Interface implemented by services which need to register jobs at startup.
 */
public interface JobProvider {

    /**
     * Gets the list of jobs to register at startup
     */
    Collection<JobRegistration> getStartingJobs();

}
