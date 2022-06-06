package net.nemerosa.ontrack.extension.av.audit

enum class AutoVersioningAuditState(
    val isRunning: Boolean = true,
) {

    CREATED,
    RECEIVED,
    ERROR(isRunning = false),
    PROCESSING_START,
    PROCESSING_ABORTED(isRunning = false),
    PROCESSING_CREATING_BRANCH,
    PROCESSING_UPDATING_FILE,
    POST_PROCESSING_START,
    POST_PROCESSING_END,
    PR_CREATING,
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
    PR_MERGED(isRunning = false)

}