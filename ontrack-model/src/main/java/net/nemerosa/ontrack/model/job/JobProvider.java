package net.nemerosa.ontrack.model.job;

import java.util.Collection;

@Deprecated
public interface JobProvider {

    /**
     * Gets the list of jobs that can run. This method is called by the job orchestrator
     * to know what jobs can be run. It is called once per minute.
     */
    Collection<Job> getJobs();

}
