package net.nemerosa.ontrack.extension.av.audit

import net.nemerosa.ontrack.model.structure.Branch

interface AutoVersioningAuditQueryService {

    fun findByUUID(branch: Branch, uuid: String): AutoVersioningAuditEntry?

    fun findByFilter(filter: AutoVersioningAuditQueryFilter, offset: Int = 0, size: Int = 10): List<AutoVersioningAuditEntry>

}