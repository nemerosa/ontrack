package net.nemerosa.ontrack.job;

/**
 * Global state of a job
 */
public enum JobState {

    /**
     * Job scheduled, but not running right now.
     */
    IDLE,

    /**
     * Job running
     */
    RUNNING,

    /**
     * Job paused
     */
    PAUSED,

    /**
     * Job disabled
     */
    DISABLED,

    /**
     * Job scheduled for deletion
     */
    INVALID

}
