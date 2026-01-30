package net.nemerosa.ontrack.extension.av.audit

import net.nemerosa.ontrack.common.api.APIDescription
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import java.time.Duration
import java.time.LocalDateTime

data class AutoVersioningAuditEntry(
    val order: AutoVersioningOrder,
    val audit: List<AutoVersioningAuditEntryState>,
    @APIDescription("Queue routing key used for this order")
    val routing: String?,
    @APIDescription("Actual queue where the order was posted")
    val queue: String?,
    @APIDescription("Actual SCM branch being used")
    val upgradeBranch: String?,
) {
    val mostRecentState: AutoVersioningAuditEntryState = audit.first()
    val running: Boolean = mostRecentState.state.isRunning
    val timestamp: LocalDateTime = mostRecentState.signature.time

    val duration: Long get() = Duration.between(audit.last().signature.time, audit.first().signature.time).toMillis()
}

