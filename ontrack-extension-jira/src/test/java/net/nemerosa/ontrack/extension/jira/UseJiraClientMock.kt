package net.nemerosa.ontrack.extension.jira

import net.nemerosa.ontrack.extension.jira.client.JIRAClient
import org.springframework.test.context.TestPropertySource

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@TestPropertySource(
    properties = [
        "${JIRAClient.PROPERTY_JIRA_CLIENT_TYPE}=${JIRAClient.PROPERTY_JIRA_CLIENT_TYPE_MOCK}"
    ]
)
annotation class UseJiraClientMock

