package net.nemerosa.ontrack.extension.jira.mock

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.jira.JIRAConfigurationProperties
import net.nemerosa.ontrack.extension.jira.model.JIRAIssue
import net.nemerosa.ontrack.extension.jira.model.JIRAStatus
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

    fun registerIssue(key: String, summary: String) {
        val projectName = key.substringBefore("-")
        val project = projects.getOrPut(projectName) {
            JIRAProject(projectName)
        }
        project.issues[key] = JIRAIssue(
            url = "mock://jira/$projectName/$key",
            key = key,
            summary = summary,
            status = JIRAStatus("Open", "mock"),
            assignee = "unknown",
            updateTime = Time.now(),
            fields = emptyList(),
            affectedVersions = emptyList(),
            fixVersions = emptyList(),
            issueType = "Defect",
            links = emptyList(),
        )
    }

    fun getIssue(key: String): JIRAIssue? {
        val projectName = key.substringBefore("-")
        return projects[projectName]?.issues?.get(key)
    }
}