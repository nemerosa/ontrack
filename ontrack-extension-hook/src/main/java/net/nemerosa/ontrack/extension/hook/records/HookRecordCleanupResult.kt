package net.nemerosa.ontrack.extension.hook.records

/**
 * Summary for the cleanup of hook records
 *
 * @property nonRunning Number of non-running records having been removed
 * @property anyState Number of records having been removed, whatever their running state
 */
class HookRecordCleanupResult(
        val nonRunning: Int,
        val anyState: Int
)