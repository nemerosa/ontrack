package net.nemerosa.ontrack.extension.queue.record

/**
 * Summary for the cleanup of queue records
 *
 * @property nonRunning Number of non-running records having been removed
 * @property anyState Number of records having been removed, whatever their running state
 */
class QueueRecordCleanupResult(
        val nonRunning: Int,
        val anyState: Int
)