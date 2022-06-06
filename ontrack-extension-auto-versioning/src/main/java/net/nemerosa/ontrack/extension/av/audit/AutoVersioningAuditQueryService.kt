package net.nemerosa.ontrack.extension.av.audit

import net.nemerosa.ontrack.model.structure.Branch

interface AutoVersioningAuditQueryService {

    fun findByUUID(branch: Branch, uuid: String): AutoVersioningAuditEntry?

    fun findByFilter(filter: AutoVersioningAuditQueryFilter): List<AutoVersioningAuditEntry>

    fun countByFilter(filter: AutoVersioningAuditQueryFilter): Int

}