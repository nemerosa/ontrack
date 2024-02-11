package net.nemerosa.ontrack.extension.jira.mock

import net.nemerosa.ontrack.extension.jira.JIRAConfiguration
import net.nemerosa.ontrack.extension.jira.client.JIRAClient
import net.nemerosa.ontrack.extension.jira.model.JIRAIssue
import net.nemerosa.ontrack.extension.jira.tx.JIRASession

class MockJIRAClient(
    private val instance: MockJIRAInstance,
    private val configuration: JIRAConfiguration
) : JIRAClient {

    override fun close() {
    }

    override fun getIssue(key: String, configuration: JIRAConfiguration): JIRAIssue? =
        instance.getIssue(key)

    override fun getProjects(): List<String> = instance.projectNames

}