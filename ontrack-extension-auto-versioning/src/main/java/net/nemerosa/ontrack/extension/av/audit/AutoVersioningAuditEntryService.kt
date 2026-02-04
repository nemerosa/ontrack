package net.nemerosa.ontrack.extension.av.audit

import net.nemerosa.ontrack.extension.scm.service.SCMPullRequest

/**
 * Helper around the audit entries.
 */
interface AutoVersioningAuditEntryService {

    /**
     * Given an audit entry, returns the associated pull request if any.
     */
    fun getPullRequest(entry: AutoVersioningAuditEntry): SCMPullRequest?

}