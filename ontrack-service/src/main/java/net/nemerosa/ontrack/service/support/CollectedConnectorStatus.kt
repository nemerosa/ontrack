package net.nemerosa.ontrack.service.support

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.model.support.CollectedConnectorStatus
import net.nemerosa.ontrack.model.support.ConnectorStatus

fun ConnectorStatus.collected() = CollectedConnectorStatus(
        status = this,
        time = Time.now()
)
