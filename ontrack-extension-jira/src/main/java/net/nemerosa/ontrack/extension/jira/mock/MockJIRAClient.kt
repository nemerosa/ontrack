package net.nemerosa.ontrack.extension.jira.mock

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.jira.JIRAConfiguration
import net.nemerosa.ontrack.extension.jira.client.JIRAClient
import net.nemerosa.ontrack.extension.jira.model.*
import net.nemerosa.ontrack.extension.jira.notifications.JiraCustomField
import java.util.concurrent.atomic.AtomicInteger

class MockJIRAClient(
    private val instance: MockJIRAInstance,
) : JIRAClient {

    private val counter = AtomicInteger(0)
    private val issues = mutableListOf<JIRAIssue>()

    override fun close() {
    }

    override fun getIssue(key: String, configuration: JIRAConfiguration): JIRAIssue? =
        instance.getIssue(key)

    override fun createIssue(
        configuration: JIRAConfiguration,
        project: String,
        issueType: String,
        labels: List<String>,
        fixVersion: String?,
        assignee: String?,
        title: String,
        customFields: List<JiraCustomField>,
        body: String
    ): JIRAIssueStub {
        val id = counter.incrementAndGet()
        val key = "$project-$id"
        val url = "${configuration.url}/browse/$key"
        val issue = JIRAIssue(
            url = url,
            key = key,
            summary = title,
            status = JIRAStatus("open", ""),
            assignee = assignee ?: "",
            updateTime = Time.now(),
            fields = customFields.map {
                JIRAField(id = it.name, name = it.name, value = it.value)
            },
            affectedVersions = emptyList(),
            fixVersions = if (fixVersion.isNullOrBlank()) {
                emptyList()
            } else {
                listOf(
                    JIRAVersion(fixVersion, false)
                )
            },
            issueType = issueType,
            links = emptyList(),
        )
        issues += issue
        return JIRAIssueStub(
            key = key,
            url = url,
        )
    }

    override fun searchIssueStubs(jiraConfiguration: JIRAConfiguration, jql: String): List<JIRAIssueStub> {
        return emptyList()
    }

    override val projects: List<String>
        get() = instance.projectNames

}