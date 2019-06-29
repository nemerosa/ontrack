package net.nemerosa.ontrack.service.support

import org.springframework.boot.actuate.endpoint.Endpoint
import org.springframework.stereotype.Component

@Component
class ConnectorStatusEndpoint(
        private val connectorStatusJob: ConnectorStatusJob
) : Endpoint<ConnectorGlobalStatus> {

    override fun isEnabled(): Boolean = true

    override fun isSensitive(): Boolean = true

    override fun getId(): String = "connectors"

    override fun invoke() = ConnectorGlobalStatus(
            connectorStatusJob.statuses.values.flatten().sortedBy {
                it.status.description.connector
            }
    )

}