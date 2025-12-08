package net.nemerosa.ontrack.extension.av.audit

import net.nemerosa.ontrack.model.structure.Branch
import java.time.LocalDateTime

interface AutoVersioningAuditQueryService {

    fun findByUUID(branch: Branch, uuid: String): AutoVersioningAuditEntry?

    fun findByFilter(filter: AutoVersioningAuditQueryFilter): List<AutoVersioningAuditEntry>

    fun countByFilter(filter: AutoVersioningAuditQueryFilter): Int

    /**
     * Looks for all entries that are ready to be processed at any given time.
     */
    fun findByReady(
        time: LocalDateTime,
    ): List<AutoVersioningAuditEntry>

}