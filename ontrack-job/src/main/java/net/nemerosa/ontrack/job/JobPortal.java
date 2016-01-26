package net.nemerosa.ontrack.job;

/**
 * The <code>JobPortal</code> is used by {@link JobProvider providers of jobs} to register their list of jobs.
 * Regularly, the registered jobs will be synchronised with the {@link JobScheduler job scheduler}.
 */
public interface JobPortal {

    /**
     * Registers a job provider
     */
    void registerJobProvider(JobProvider jobProvider);

    /**
     * Access to the underlying scheduler
     */
    JobScheduler getJobScheduler();

}
