package net.nemerosa.ontrack.job;

import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public interface Job {

    /**
     * Key of the job
     */
    JobKey getKey();

    /**
     * Task to be run by the job
     */
    JobRun getTask();

    /**
     * Gets a description for the job
     */
    String getDescription();

    /**
     * Is the job disabled for the next run?
     */
    boolean isDisabled();

    /**
     * Optional timeout for running this job.
     * <p>
     * If null (the default), no timeout is defined by the job but general settings may apply.
     */
    default @Nullable
    Duration getTimeout() {
        return null;
    }

    /**
     * This method is called to see if the job is still valid. It is called prior any execution
     * and when collecting the job status. By default, returns <code>true</code>.
     * <p>
     * For example, jobs which depend on properties or configurations, can just test the existence of the property
     * or configuration.
     */
    default boolean isValid() {
        return true;
    }

}
