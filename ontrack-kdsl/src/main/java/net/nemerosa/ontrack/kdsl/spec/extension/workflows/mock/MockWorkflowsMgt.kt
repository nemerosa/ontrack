package net.nemerosa.ontrack.kdsl.spec.extension.workflows.mock

import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.MockWorkflowTextsQuery
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector

class MockWorkflowsMgt(connector: Connector) : Connected(connector) {
    fun getTexts(instanceId: String): List<String> {
        return graphqlConnector.query(
            MockWorkflowTextsQuery(instanceId)
        )?.mockWorkflowTexts ?: emptyList()
    }
}