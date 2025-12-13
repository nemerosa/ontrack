package net.nemerosa.ontrack.extension.jira.mock

import net.nemerosa.ontrack.extension.jira.JIRAConfiguration
import net.nemerosa.ontrack.extension.jira.client.JIRAClient.Companion.PROPERTY_JIRA_CLIENT_TYPE
import net.nemerosa.ontrack.extension.jira.client.JIRAClient.Companion.PROPERTY_JIRA_CLIENT_TYPE_MOCK
import net.nemerosa.ontrack.extension.jira.tx.JIRASession
import net.nemerosa.ontrack.extension.jira.tx.JIRASessionFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    name = [PROPERTY_JIRA_CLIENT_TYPE],
    havingValue = PROPERTY_JIRA_CLIENT_TYPE_MOCK,
    matchIfMissing = false,
)
class MockJIRASessionFactory(
    private val instance: MockJIRAInstance,
) : JIRASessionFactory {

    override fun create(configuration: JIRAConfiguration): JIRASession = MockJIRASession(instance)
}