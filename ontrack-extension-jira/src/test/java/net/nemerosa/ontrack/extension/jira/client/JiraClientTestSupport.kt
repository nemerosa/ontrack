package net.nemerosa.ontrack.extension.jira.client

import net.nemerosa.ontrack.extension.jira.JIRAConfiguration
import net.nemerosa.ontrack.extension.jira.JIRAConfigurationService
import net.nemerosa.ontrack.extension.jira.tx.JIRASessionFactory
import net.nemerosa.ontrack.test.TestUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class JiraClientTestSupport {

    @Autowired
    private lateinit var jiraSessionFactory: JIRASessionFactory

    @Autowired
    private lateinit var jiraConfigurationService: JIRAConfigurationService


    /**
     * Creating a client
     */
    fun withRealJiraClient(
        code: (client: JIRAClient, config: JIRAConfiguration) -> Unit,
    ) {
        withRealJiraConfig { config ->
            val client = jiraSessionFactory.create(config).client
            code(client, config)
        }
    }

    /**
     * Creating a Jira configuration
     */
    fun withRealJiraConfig(
        code: (config: JIRAConfiguration) -> Unit,
    ) {
        val env = jiraClientEnv
        val configName = TestUtils.uid("jira-real-")
        val config = JIRAConfiguration(
            name = configName,
            url = env.url,
            user = env.username,
            password = env.password,
            include = emptyList(),
            exclude = emptyList(),
        )
        jiraConfigurationService.newConfiguration(config)
        code(config)
    }
}