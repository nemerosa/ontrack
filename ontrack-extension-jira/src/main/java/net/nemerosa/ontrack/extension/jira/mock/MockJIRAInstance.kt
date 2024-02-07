package net.nemerosa.ontrack.extension.jira.mock

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.jira.JIRAConfigurationProperties
import net.nemerosa.ontrack.extension.jira.model.JIRAIssue
import net.nemerosa.ontrack.extension.jira.model.JIRALink
import net.nemerosa.ontrack.extension.jira.model.JIRAStatus
import net.nemerosa.ontrack.extension.jira.model.JIRAVersion
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    prefix = JIRAConfigurationProperties.JIRA_MOCK_PREFIX,
    name = [JIRAConfigurationProperties.JIRA_MOCK_ENABLED],
    havingValue = "true",
    matchIfMissing = false,
)
class MockJIRAInstance {

    private class JIRAProject(
        val name: String,
        val issues: MutableMap<String, JIRAIssue> = mutableMapOf(),
    )

    private val projects = mutableMapOf<String, JIRAProject>()

    val projectNames: List<String> get() = projects.keys.sorted()

    fun registerIssue(key: String, summary: String, linkedKey: String? = null): JIRAIssue {
        val projectName = key.substringBefore("-")
        val project = projects.getOrPut(projectName) {
            JIRAProject(projectName)
        }
        val openStatus = JIRAStatus("Open", "https://mock/status/Open")
        val issue = JIRAIssue(
            url = "mock://jira/$projectName/$key",
            key = key,
            summary = summary,
            status = openStatus,
            assignee = "unknown",
            updateTime = Time.now(),
            fields = emptyList(),
            affectedVersions = listOf(
                JIRAVersion("4.6", true),
                JIRAVersion("4.7", true),
            ),
            fixVersions = listOf(
                JIRAVersion("4.8", false),
            ),
            issueType = "Defect",
            links = if (linkedKey.isNullOrBlank()) {
                emptyList()
            } else {
                listOf(
                    JIRALink(
                        key = linkedKey,
                        url = "mock://jira/$projectName/$linkedKey",
                        status = openStatus,
                        linkName = "Relates to",
                        link = "Relates to",
                    )
                )
            },
        )
        project.issues[key] = issue
        return issue
    }

    fun getIssue(key: String): JIRAIssue? {
        val projectName = key.substringBefore("-")
        return projects[projectName]?.issues?.get(key)
    }
}