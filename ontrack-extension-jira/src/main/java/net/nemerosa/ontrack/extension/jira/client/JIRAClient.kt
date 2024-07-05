package net.nemerosa.ontrack.extension.jira.client

import net.nemerosa.ontrack.extension.jira.JIRAConfiguration
import net.nemerosa.ontrack.extension.jira.model.JIRAIssue
import net.nemerosa.ontrack.extension.jira.model.JIRAIssueStub
import net.nemerosa.ontrack.extension.jira.notifications.JiraCustomField
import org.springframework.web.client.RestTemplate

interface JIRAClient : AutoCloseable {

    fun getIssue(key: String, configuration: JIRAConfiguration): JIRAIssue?

    val projects: List<String>

    fun createIssue(
        configuration: JIRAConfiguration,
        project: String,
        issueType: String,
        labels: List<String> = emptyList(),
        fixVersion: String? = null,
        assignee: String? = null,
        title: String,
        customFields: List<JiraCustomField> = emptyList(),
        body: String,
    ): JIRAIssueStub

    fun searchIssueStubs(
        jiraConfiguration: JIRAConfiguration,
        jql: String,
    ): List<JIRAIssueStub>

    /**
     * Creates a link between two tickets
     *
     * @param jiraConfiguration Jira configuration
     * @param sourceTicket Key of the source ticket
     * @param targetTicket Key of the target ticket
     * @param linkName Name of the link
     */
    fun createLink(
        jiraConfiguration: JIRAConfiguration,
        sourceTicket: String,
        targetTicket: String,
        linkName: String,
    )

    /**
     * Underlying REST template to use
     */
    val restTemplate: RestTemplate

}
