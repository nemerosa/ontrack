package net.nemerosa.ontrack.model.support

import java.time.LocalDateTime

data class CollectedConnectorStatus(
        val status: ConnectorStatus,
        val time: LocalDateTime
)