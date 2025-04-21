package net.nemerosa.ontrack.extension.jira.mock

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.jira.JIRAConfiguration
import net.nemerosa.ontrack.extension.jira.client.JIRAClient
import net.nemerosa.ontrack.extension.jira.model.*
import net.nemerosa.ontrack.extension.jira.notifications.JiraCustomField
import org.springframework.web.client.RestTemplate
import java.util.concurrent.atomic.AtomicInteger

class MockJIRAClient(
    private val instance: MockJIRAInstance
) : JIRAClient {

    private val counter = AtomicInteger(0)

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
        instance.registerIssue(issue)
        return JIRAIssueStub(
            key = key,
            url = url,
        )
    }

    override fun createLink(
        jiraConfiguration: JIRAConfiguration,
        sourceTicket: String,
        targetTicket: String,
        linkName: String
    ) {
        val source = instance.getIssue(sourceTicket) ?: error("Source ticket not found")
        val target = instance.getIssue(sourceTicket) ?: error("Target ticket not found")
        val linkedSource = source.withLinks(
            listOf(
                JIRALink(
                    source.key,
                    source.url,
                    JIRAStatus("Open", "${jiraConfiguration.url}/images/icons/statuses/open.png"),
                    linkName,
                    linkName
                )
            )
        )
        val linkedTarget = target.withLinks(
            listOf(
                JIRALink(
                    target.key,
                    target.url,
                    JIRAStatus("Open", "${jiraConfiguration.url}/images/icons/statuses/open.png"),
                    linkName,
                    "$linkName-inverse"
                )
            )
        )
        instance.registerIssue(linkedSource)
        instance.registerIssue(linkedTarget)
    }

    private val jqlRegex = """key\s*=\s*(.*)""".toRegex()

    override fun searchIssueStubs(jiraConfiguration: JIRAConfiguration, jql: String): List<JIRAIssueStub> {
        val m = jqlRegex.matchEntire(jql)
        return if (m != null) {
            val key = m.groupValues[1]
            listOfNotNull(
                instance.getIssue(key)?.toStub()
            )
        } else {
            emptyList()
        }
    }

    override val projects: List<String>
        get() = instance.projectNames

    override val restTemplate: RestTemplate
        get() = error("Not supported for MockJIRAClient")
}