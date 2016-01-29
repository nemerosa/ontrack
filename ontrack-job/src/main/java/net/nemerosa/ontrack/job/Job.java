package net.nemerosa.ontrack.job;

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
     * This method is called to see if the job is still valid. It is called prior any execution
     * and when collecting the job status. By default, returns <code>true</code>.
     */
    default boolean isValid() {
        return true;
    }

}
