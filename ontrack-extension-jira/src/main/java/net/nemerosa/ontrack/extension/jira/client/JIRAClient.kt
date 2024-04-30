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
        labels: List<String>,
        fixVersion: String?,
        assignee: String?,
        title: String,
        customFields: List<JiraCustomField>,
        body: String,
    ): JIRAIssueStub

    fun searchIssueStubs(
        jiraConfiguration: JIRAConfiguration,
        jql: String,
    ): List<JIRAIssueStub>

    /**
     * Underlying REST template to use
     */
    val restTemplate: RestTemplate

}
