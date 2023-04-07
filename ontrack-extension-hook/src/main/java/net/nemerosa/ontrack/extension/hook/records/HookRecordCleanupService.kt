package net.nemerosa.ontrack.extension.hook.records

interface HookRecordCleanupService {

    /**
     * Removes all non-running entries which are older than a given retention period.
     *
     * @return Summary of the cleanup
     */
    fun cleanup(): HookRecordCleanupResult

    /**
     * Removes ALL entries, whatever their state.
     */
    fun purge()

}