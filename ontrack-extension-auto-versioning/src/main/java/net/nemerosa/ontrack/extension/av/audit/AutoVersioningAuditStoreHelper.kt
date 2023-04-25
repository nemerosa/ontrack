package net.nemerosa.ontrack.extension.av.audit

/**
 * Helper to supplement the fact that the entity data store of Ontrack
 * does not allow for queries outside of entities.
 */
@Deprecated("Use recordings interface")
interface AutoVersioningAuditStoreHelper {

    /**
     * Counts the number of auto versioning audit entries which are in the given [state]
     *
     * @param state State to look for
     * @return Number of auto versioning audit entries which are in the given [state]
     */
    fun countByState(state: AutoVersioningAuditState): Int

    /**
     * Global query to get audit versioning entries.
     *
     * @param filter Filter to use
     * @return List of audit entries matching the [filter]
     */
    fun auditVersioningEntries(
        filter: AutoVersioningAuditQueryFilter
    ): List<AutoVersioningAuditEntry>

    /**
     * Global query to get the count versioning entries.
     *
     * @param filter Filter to use
     * @return Count of audit entries matching the [filter]
     */
    fun auditVersioningEntriesCount(
        filter: AutoVersioningAuditQueryFilter
    ): Int
}