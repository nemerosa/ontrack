package net.nemerosa.ontrack.service.support

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.model.support.ConnectorStatus
import java.time.LocalDateTime

data class CollectedConnectorStatus(
        val status: ConnectorStatus,
        val time: LocalDateTime
)

fun ConnectorStatus.collected() = CollectedConnectorStatus(
        status = this,
        time = Time.now()
)
