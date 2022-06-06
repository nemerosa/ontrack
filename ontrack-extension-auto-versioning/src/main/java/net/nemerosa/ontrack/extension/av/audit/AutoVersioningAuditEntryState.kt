package net.nemerosa.ontrack.extension.av.audit

import net.nemerosa.ontrack.model.structure.Signature

data class AutoVersioningAuditEntryState(
    val signature: Signature,
    val state: AutoVersioningAuditState,
    val data: Map<String, String>,
)