package net.nemerosa.ontrack.extension.jira.mock

import net.nemerosa.ontrack.extension.jira.client.JIRAClient
import net.nemerosa.ontrack.extension.jira.tx.JIRASession

class MockJIRASession(
    private val instance: MockJIRAInstance,
) : JIRASession {

    override fun close() {}

    override fun getClient(): JIRAClient = MockJIRAClient(instance)
}