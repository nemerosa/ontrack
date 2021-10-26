package net.nemerosa.ontrack.job.support

/**
 * Wraps an [Error] caught while running a job.
 */
class JobErrorWrapperException(
    error: Error
) : RuntimeException(
    "Uncaught error while running a job",
    error
)