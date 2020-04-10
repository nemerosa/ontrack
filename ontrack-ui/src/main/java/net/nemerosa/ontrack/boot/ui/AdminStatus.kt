package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.support.ConnectorGlobalStatus
import org.springframework.boot.actuate.health.HealthComponent

class AdminStatus(
        val health: HealthComponent,
        val connectors: ConnectorGlobalStatus
)