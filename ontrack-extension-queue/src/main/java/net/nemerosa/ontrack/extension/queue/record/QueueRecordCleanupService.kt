package net.nemerosa.ontrack.extension.queue.record

interface QueueRecordCleanupService {

    /**
     * Removes all non-running entries which are older than a given retention period.
     *
     * @return Summary of the cleanup
     */
    fun cleanup(): QueueRecordCleanupResult

    /**
     * Removes ALL entries, whatever their state.
     */
    fun purge()

}