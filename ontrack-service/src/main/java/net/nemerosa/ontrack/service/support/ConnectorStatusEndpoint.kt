package net.nemerosa.ontrack.service.support

import net.nemerosa.ontrack.model.support.ConnectorGlobalStatus
import org.springframework.boot.actuate.endpoint.AbstractEndpoint
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "endpoints.ontrack-connectors")
class ConnectorStatusEndpoint(
        private val connectorStatusJob: ConnectorStatusJob
) : AbstractEndpoint<ConnectorGlobalStatus>("connectors", true) {

    override fun invoke() = ConnectorGlobalStatus(
            connectorStatusJob.statuses.values.flatten().sortedBy {
                it.status.description.connector
            }
    )

}