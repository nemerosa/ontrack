package net.nemerosa.ontrack.extension.jira.mock

import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.jira.JIRAConfiguration
import net.nemerosa.ontrack.extension.jira.tx.JIRASession
import net.nemerosa.ontrack.extension.jira.tx.JIRASessionFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile(RunProfile.DEV)
class MockJIRASessionFactory(
    private val instance: MockJIRAInstance,
) : JIRASessionFactory {

    override fun create(configuration: JIRAConfiguration): JIRASession = MockJIRASession(instance)
}