package net.nemerosa.ontrack.graphql.schema.health

import net.nemerosa.ontrack.model.support.ConnectorGlobalStatus
import org.springframework.boot.actuate.health.HealthComponent

data class SystemHealth(
    val health: HealthComponent,
    val connectors: ConnectorGlobalStatus,
)
