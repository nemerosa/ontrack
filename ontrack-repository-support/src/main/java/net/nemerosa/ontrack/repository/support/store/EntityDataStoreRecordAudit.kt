package net.nemerosa.ontrack.repository.support.store

import net.nemerosa.ontrack.model.structure.Signature

data class EntityDataStoreRecordAudit(
    val type: EntityDataStoreRecordAuditType,
    val signature: Signature,
)

