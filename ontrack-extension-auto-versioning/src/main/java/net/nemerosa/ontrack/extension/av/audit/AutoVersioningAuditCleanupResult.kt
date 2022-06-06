package net.nemerosa.ontrack.extension.av.audit

/**
 * Summary for the cleanup of auto versioning audit
 *
 * @property nonRunning Number of non-running audit entries having been removed
 * @property anyState Number of audit entries having been removed, whatever their running state
 */
class AutoVersioningAuditCleanupResult(
    val nonRunning: Int,
    val anyState: Int
)