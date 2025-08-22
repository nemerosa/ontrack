package net.nemerosa.ontrack.extension.issues.mock

import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration
import net.nemerosa.ontrack.extension.issues.support.AbstractIssueServiceExtension
import net.nemerosa.ontrack.model.support.MessageAnnotation
import net.nemerosa.ontrack.model.support.MessageAnnotator
import net.nemerosa.ontrack.model.support.RegexMessageAnnotator
import org.springframework.stereotype.Component

@Component
class TestIssueServiceExtension(
    extensionFeature: TestIssueServiceFeature,
) : AbstractIssueServiceExtension(
    extensionFeature,
    "test",
    "Test issues",
) {

    /**
     * Issues registered for testing
     */
    private val issues = mutableMapOf<Int, TestIssue>()

    /**
     * Resets the list of registered issues
     */
    fun resetIssues() {
        issues.clear()
    }

    /**
     * Registers some issues
     */
    fun register(vararg issues: TestIssue) {
        issues.forEach {
            this.issues[it.key.toInt()] = it
        }
    }

    override fun getConfigurationList(): List<IssueServiceConfiguration> =
        listOf(TestIssueServiceConfiguration.INSTANCE)

    override fun getConfigurationByName(name: String): IssueServiceConfiguration? =
        if (name == TestIssueServiceConfiguration.INSTANCE.name) {
            TestIssueServiceConfiguration.INSTANCE
        } else {
            null
        }

    private fun validIssueToken(token: String): Boolean = token.matches("#(\\d+)".toRegex())

    override fun getDisplayKey(issueServiceConfiguration: IssueServiceConfiguration, key: String): String =
        if (key.startsWith("#")) {
            key
        } else {
            "#$key"
        }

    override fun extractIssueKeysFromMessage(
        issueServiceConfiguration: IssueServiceConfiguration?,
        message: String?
    ): MutableSet<String> {
        val result = mutableSetOf<String>()
        if (!message.isNullOrBlank()) {
            "#(\\d+)".toRegex().findAll(message).forEach { mr ->
                val issueKey = mr.groupValues[1]
                result += issueKey
            }
        }
        // OK
        return result;
    }

    override fun getMessageAnnotator(issueServiceConfiguration: IssueServiceConfiguration): MessageAnnotator? {
        return RegexMessageAnnotator("(#\\d+)".toRegex()) { token ->
            MessageAnnotation.of("a")
                .attr("href", "http://issue/${token.substring(1)}")
                .text(token)
        }
    }

    override fun getIssue(issueServiceConfiguration: IssueServiceConfiguration, issueKey: String): Issue? {
        val key = getIssueId(issueKey)
        return issues[key] ?: TestIssue(
            key,
            TestIssueStatus.OPEN,
            "bug"
        )
    }

    protected fun getIssueId(issueKey: String): Int {
        return issueKey.trimStart('#').toInt()
    }

    override fun getIssueId(issueServiceConfiguration: IssueServiceConfiguration, token: String): String? {
        val value = token.toIntOrNull()
        return if (value != null && validIssueToken(token)) {
            getIssueId(token).toString()
        } else {
            null
        }
    }

    override fun getIssueTypes(
        issueServiceConfiguration: IssueServiceConfiguration,
        issue: Issue
    ): Set<String> = if (issue is TestIssue && issue.type != null) {
        setOf(issue.type)
    } else {
        emptySet()
    }
}
