package net.nemerosa.ontrack.extension.av.audit

interface AutoVersioningAuditCleanupService {

    /**
     * Removes all non-running entries which are older than a given retention period.
     *
     * @return Summary of the cleanup
     */
    fun cleanup(): AutoVersioningAuditCleanupResult

    /**
     * Removes ALL entries, whatever their state.
     */
    fun purge()

}