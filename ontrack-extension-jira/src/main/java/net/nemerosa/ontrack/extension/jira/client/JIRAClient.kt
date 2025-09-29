package net.nemerosa.ontrack.extension.jira.client

import net.nemerosa.ontrack.extension.jira.JIRAConfiguration
import net.nemerosa.ontrack.extension.jira.model.JIRAIssue
import net.nemerosa.ontrack.extension.jira.model.JIRAIssueStub
import net.nemerosa.ontrack.extension.jira.notifications.JiraCustomField
import org.springframework.web.client.RestTemplate

interface JIRAClient : AutoCloseable {

    fun getIssue(key: String, configuration: JIRAConfiguration): JIRAIssue?

    /**
     * Getting the last commit for a given issue.
     *
     * @param key Key of the issue
     * @param configuration Jira configuration
     * @param applicationType Type of application providing the meta-information (stash, github, ...)
     * @param repositoryName Name of the repository as recorded by Jira
     */
    fun getIssueLastCommit(
        key: String,
        configuration: JIRAConfiguration,
        applicationType: String,
        repositoryName: String,
    ): String?

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

    companion object {
        const val PROPERTY_JIRA_CLIENT_TYPE = "ontrack.extension.jira.client.type"
        const val PROPERTY_JIRA_CLIENT_TYPE_DEFAULT = "default"
        const val PROPERTY_JIRA_CLIENT_TYPE_MOCK = "mock"
    }

}
