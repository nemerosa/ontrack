package net.nemerosa.ontrack.extension.av.audit

enum class AutoVersioningAuditState(
    val isRunning: Boolean = true,
    val isProcessing: Boolean = false,
) {

    CREATED,
    RECEIVED,
    ERROR(isRunning = false),
    PROCESSING_START(isProcessing = true),
    PROCESSING_ABORTED(isRunning = false),

    /**
     * Processing was cancelled because there is more recent request on the same target branch.
     */
    PROCESSING_CANCELLED(isRunning = false),
    PROCESSING_CREATING_BRANCH(isProcessing = true),
    PROCESSING_UPDATING_FILE(isProcessing = true),
    POST_PROCESSING_START(isProcessing = true),
    POST_PROCESSING_END(isProcessing = true),
    PR_CREATING(isProcessing = true),
    PR_TIMEOUT(isRunning = false),

    /**
     * PR was just created (no auto approval)
     */
    PR_CREATED(isRunning = false),

    /**
     * PR was created and approved, but merge is delegated
     */
    PR_APPROVED(isRunning = false),

    /**
     * PR was merged
     */
    PR_MERGED(isRunning = false);

    companion object {
        val runningAndNotProcessingStates = values().filter { it.isRunning && !it.isProcessing }.toSet()
    }

}