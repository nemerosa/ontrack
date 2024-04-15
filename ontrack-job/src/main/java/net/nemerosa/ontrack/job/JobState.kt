package net.nemerosa.ontrack.job

/**
 * Global state of a job
 */
enum class JobState(
    val displayName: String,
    val description: String,
) {

    /**
     * Job scheduled, but not running right now.
     */
    IDLE(
        displayName = "Idle",
        description = "Job scheduled, but not running right now.",
    ),

    /**
     * Job running
     */
    RUNNING(
        displayName = "Running",
        description = "Job running",
    ),

    /**
     * Job paused
     */
    PAUSED(
        displayName = "Paused",
        description = "Job paused",
    ),

    /**
     * Job disabled
     */
    DISABLED(
        displayName = "Disabled",
        description = "Job disabled",
    ),

    /**
     * Job scheduled for deletion
     */
    INVALID(
        displayName = "Invalid",
        description = "Job scheduled for deletion",
    )

}
