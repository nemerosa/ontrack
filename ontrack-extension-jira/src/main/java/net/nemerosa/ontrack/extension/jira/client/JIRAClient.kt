package net.nemerosa.ontrack.extension.jira.client

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.jira.JIRAConfiguration
import net.nemerosa.ontrack.extension.jira.model.JIRAIssue
import net.nemerosa.ontrack.extension.jira.model.JIRAIssueStub

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
        customFields: Map<String, JsonNode>,
        body: String,
    ): JIRAIssueStub

}
