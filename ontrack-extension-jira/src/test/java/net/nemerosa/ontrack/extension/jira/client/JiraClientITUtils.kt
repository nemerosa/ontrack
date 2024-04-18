package net.nemerosa.ontrack.extension.jira.client

import net.nemerosa.ontrack.test.getOptionalEnv
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIf

data class JiraClientEnv(
    val enabled: Boolean,
    val url: String,
    val username: String,
    val password: String,
    val project: String,
    val issueType: String,
)

val jiraClientEnv: JiraClientEnv by lazy {
    JiraClientEnv(
        enabled = getOptionalEnv("ontrack.test.extension.jira.enabled") == "true",
        url = getOptionalEnv("ontrack.test.extension.jira.url") ?: "http://localhost:8080",
        username = getOptionalEnv("ontrack.test.extension.jira.username") ?: "admin",
        password = getOptionalEnv("ontrack.test.extension.jira.password") ?: "admin",
        project = getOptionalEnv("ontrack.test.extension.jira.project") ?: "TEST",
        issueType = getOptionalEnv("ontrack.test.extension.jira.type") ?: "Bug",
    )
}

/**
 * Annotation to use on tests relying on an local Jira Server.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Test
@EnabledIf("net.nemerosa.ontrack.extension.jira.client.TestOnJiraServerCondition#isTestOnJiraServerEnabled")
annotation class TestOnJiraServer

/**
 * Testing if the environment is set for testing against GitHub.
 *
 * Used by [TestOnJiraServer].
 */
@Suppress("unused")
class TestOnJiraServerCondition {

    companion object {
        @JvmStatic
        fun isTestOnJiraServerEnabled(): Boolean =
            getOptionalEnv("ontrack.test.extension.jira.enabled") == "true"
    }
}