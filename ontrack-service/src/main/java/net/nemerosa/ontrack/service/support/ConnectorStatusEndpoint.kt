package net.nemerosa.ontrack.service.support

import org.springframework.boot.actuate.endpoint.Endpoint
import org.springframework.stereotype.Component

@Component
class ConnectorStatusEndpoint(
        private val connectorStatusJob: ConnectorStatusJob
) : Endpoint<List<CollectedConnectorStatus>> {

    override fun isEnabled(): Boolean = true

    override fun isSensitive(): Boolean = true

    override fun getId(): String = "connectors"

    override fun invoke(): List<CollectedConnectorStatus> =
            connectorStatusJob.statuses.values.flatten().sortedBy {
                it.status.description.connector
            }

}