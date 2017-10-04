package net.nemerosa.ontrack.job

/**
 * Global state of a job
 */
enum class JobState(val description: String) {

    /**
     * Job scheduled, but not running right now.
     */
    IDLE("Idle"),

    /**
     * Job running
     */
    RUNNING("Running"),

    /**
     * Job paused
     */
    PAUSED("Paused"),

    /**
     * Job disabled
     */
    DISABLED("Disabled"),

    /**
     * Job scheduled for deletion
     */
    INVALID("Invalid")

}
