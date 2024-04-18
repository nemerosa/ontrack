package net.nemerosa.ontrack.extension.jira.client

import com.fasterxml.jackson.databind.node.TextNode
import net.nemerosa.ontrack.extension.jira.notifications.JiraCustomField
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@TestOnJiraServer
class JiraClientRealIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var jiraClientTestSupport: JiraClientTestSupport

    @Test
    fun `Creating an issue`() {
        asAdmin {
            jiraClientTestSupport.withRealJiraClient { client, config ->
                val (key, url) = client.createIssue(
                    configuration = config,
                    project = jiraClientEnv.project,
                    issueType = jiraClientEnv.issueType,
                    labels = listOf(
                        "sample-label"
                    ),
                    fixVersion = null,
                    assignee = "admin",
                    title = "Sample title",
                    body = """
                        h3. Sample header
                        
                        *Sample* description
                        
                        [Sample link|https://github.com/nemerosa/ontrack]
                    """.trimIndent(),
                    customFields = listOf(
                        JiraCustomField(
                            name = "duedate",
                            value = TextNode("2024-04-18")
                        ),
                    )
                )
                assertTrue(key.isNotBlank())
                assertEquals(
                    "${jiraClientEnv.url}/browse/$key",
                    url
                )

                // Getting the issue back
                assertNotNull(client.getIssue(key, config)) { issue ->
                    assertEquals("Sample title", issue.summary)
                }
            }
        }
    }

    @Test
    fun `Looking for an issue`() {
        asAdmin {
            jiraClientTestSupport.withRealJiraClient { client, config ->
                // Unique label
                val label = uid("test-")
                // Creating the issue
                val (key, _) = client.createIssue(
                    configuration = config,
                    project = jiraClientEnv.project,
                    issueType = jiraClientEnv.issueType,
                    labels = listOf(label),
                    fixVersion = null,
                    assignee = null,
                    title = "Sample title",
                    body = """
                        Sample description
                    """.trimIndent(),
                    customFields = emptyList(),
                )

                // JQL
                val jql =
                    """project = ${jiraClientEnv.project} AND issuetype = ${jiraClientEnv.issueType} AND labels ="$label""""
                val keys = client.searchIssueStubs(config, jql).map { it.key }
                assertEquals(
                    listOf(key),
                    keys
                )
            }
        }
    }

}