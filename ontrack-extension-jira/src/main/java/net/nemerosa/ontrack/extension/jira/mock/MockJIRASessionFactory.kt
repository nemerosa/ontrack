package net.nemerosa.ontrack.extension.jira.mock

import net.nemerosa.ontrack.extension.jira.JIRAConfiguration
import net.nemerosa.ontrack.extension.jira.JIRAConfigurationProperties
import net.nemerosa.ontrack.extension.jira.tx.JIRASession
import net.nemerosa.ontrack.extension.jira.tx.JIRASessionFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    prefix = JIRAConfigurationProperties.JIRA_MOCK_PREFIX,
    name = [JIRAConfigurationProperties.JIRA_MOCK_ENABLED],
    havingValue = "true",
    matchIfMissing = false,
)
class MockJIRASessionFactory(
    private val instance: MockJIRAInstance,
) : JIRASessionFactory {

    override fun create(configuration: JIRAConfiguration): JIRASession = MockJIRASession(instance, configuration)
}