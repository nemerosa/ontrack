package net.nemerosa.ontrack.extension.av.audit

import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import java.time.Duration

data class AutoVersioningAuditEntry(
    val order: AutoVersioningOrder,
    val audit: List<AutoVersioningAuditEntryState>,
) {
    val mostRecentState: AutoVersioningAuditEntryState get() = audit.first()
    val running: Boolean get() = mostRecentState.state.isRunning

    val duration: Long get() = Duration.between(audit.last().signature.time, audit.first().signature.time).toMillis()
}

