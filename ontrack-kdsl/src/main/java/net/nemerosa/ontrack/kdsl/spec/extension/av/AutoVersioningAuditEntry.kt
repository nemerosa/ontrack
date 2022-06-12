package net.nemerosa.ontrack.kdsl.spec.extension.av

data class AutoVersioningAuditEntry(
    val order: AutoVersioningOrder,
    val running: Boolean,
    val mostRecentState: AutoVersioningAuditEntryState,
    val audit: List<AutoVersioningAuditEntryState>
)

