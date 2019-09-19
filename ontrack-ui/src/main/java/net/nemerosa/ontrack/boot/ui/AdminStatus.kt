package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.support.ConnectorGlobalStatus
import org.springframework.boot.actuate.health.Health

class AdminStatus(
        val health: Health,
        val connectors: ConnectorGlobalStatus
)