package net.nemerosa.ontrack.graphql.schema.health

import net.nemerosa.ontrack.model.support.*
import org.springframework.stereotype.Component

@Component
class TestConnectorStatusIndicator: ConnectorStatusIndicator {
    override val type: String = "test"
    override val statuses: List<ConnectorStatus> = listOf(
        ConnectorStatus(
            description = ConnectorDescription(
                connector = Connector(
                    type = "test",
                    name = "Test 1",
                ),
                connection = "testing 1"
            ),
            type = ConnectorStatusType.UP,
            error = null,
        )
    )
}