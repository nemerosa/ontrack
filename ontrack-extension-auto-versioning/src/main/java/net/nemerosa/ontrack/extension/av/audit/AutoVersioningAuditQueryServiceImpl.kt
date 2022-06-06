package net.nemerosa.ontrack.extension.av.audit

import net.nemerosa.ontrack.model.structure.Branch
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AutoVersioningAuditQueryServiceImpl(
    private val store: AutoVersioningAuditStore,
) : AutoVersioningAuditQueryService {

    override fun findByUUID(branch: Branch, uuid: String): AutoVersioningAuditEntry? =
        store.findByUUID(branch, uuid)

    override fun findByFilter(filter: AutoVersioningAuditQueryFilter): List<AutoVersioningAuditEntry> =
        store.findByFilter(filter)

    override fun countByFilter(filter: AutoVersioningAuditQueryFilter): Int =
        store.countByFilter(filter)

}