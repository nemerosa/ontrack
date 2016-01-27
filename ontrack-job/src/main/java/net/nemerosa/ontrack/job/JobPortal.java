package net.nemerosa.ontrack.job;

import net.nemerosa.ontrack.job.support.JobNotScheduledException;

import java.util.Optional;
import java.util.concurrent.Future;

/**
 * The <code>JobPortal</code> is used by {@link JobDefinitionProvider providers of jobs} to register their list of jobs.
 * Regularly, the registered jobs will be synchronised with the {@link JobScheduler job scheduler}.
 */
public interface JobPortal {

    /**
     * Registers a job provider
     */
    void registerJobProvider(JobDefinitionProvider jobProvider);

    /**
     * Access to the underlying scheduler
     */
    JobScheduler getJobScheduler();

    /**
     * Fires a job key
     */
    default Future<?> fireImmediately(JobKey key) {
        return getJobScheduler().fireImmediately(key);
    }

    /**
     * Fires a job key if possible
     */
    default Optional<Future<?>> fireImmediatelyIfPossible(JobKey key) {
        try {
            return Optional.of(getJobScheduler().fireImmediately(key));
        } catch (JobNotScheduledException ex) {
            return Optional.empty();
        }
    }

    /**
     * Fires the job portal registration now
     */
    Future<?> fire();
}
