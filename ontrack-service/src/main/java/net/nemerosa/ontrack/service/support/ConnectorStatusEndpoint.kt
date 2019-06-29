package net.nemerosa.ontrack.service.support

import net.nemerosa.ontrack.model.support.ConnectorStatus
import net.nemerosa.ontrack.model.support.ConnectorStatusIndicator
import org.springframework.boot.actuate.endpoint.Endpoint
import org.springframework.stereotype.Component

@Component
class ConnectorStatusEndpoint(
        private val connectorStatusIndicators: List<ConnectorStatusIndicator>
) : Endpoint<List<ConnectorStatus>> {

    override fun isEnabled(): Boolean = true

    override fun isSensitive(): Boolean = true

    override fun getId(): String = "connectors"

    override fun invoke(): List<ConnectorStatus> =
            connectorStatusIndicators.flatMap {
                it.statuses
            }.sortedBy {
                it.description.connector
            }

}