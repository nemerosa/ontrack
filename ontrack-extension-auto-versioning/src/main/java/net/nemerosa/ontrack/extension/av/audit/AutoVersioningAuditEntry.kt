package net.nemerosa.ontrack.extension.av.audit

import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.extension.recordings.Recording
import net.nemerosa.ontrack.model.annotations.APIDescription
import java.time.Duration
import java.time.LocalDateTime

data class AutoVersioningAuditEntry(
    val order: AutoVersioningOrder,
    val audit: List<AutoVersioningAuditEntryState>,
    @APIDescription("Queue routing key used for this order")
    val routing: String,
    @APIDescription("Actual queue where the order was posted")
    val queue: String?,
): Recording {
    val mostRecentState: AutoVersioningAuditEntryState get() = audit.first()
    val running: Boolean get() = mostRecentState.state.isRunning

    val duration: Long get() = Duration.between(audit.last().signature.time, audit.first().signature.time).toMillis()

    override val id: String = order.uuid

    override val startTime: LocalDateTime
        get() = TODO("Not yet implemented")
    override val endTime: LocalDateTime?
        get() = TODO("Not yet implemented")
}

