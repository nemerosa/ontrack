package net.nemerosa.ontrack.extension.jira.notifications

import com.fasterxml.jackson.databind.node.TextNode
import net.nemerosa.ontrack.extension.jira.JIRAConfigurationService
import net.nemerosa.ontrack.extension.jira.JIRAFixtures
import net.nemerosa.ontrack.extension.notifications.AbstractNotificationTestSupport
import net.nemerosa.ontrack.extension.notifications.subscriptions.subscribe
import net.nemerosa.ontrack.extension.support.client.MockRestTemplateContext
import net.nemerosa.ontrack.extension.support.client.MockRestTemplateProvider
import net.nemerosa.ontrack.extension.support.client.success
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
    properties = [
        "ontrack.config.extension.support.client.resttemplate=mock",
    ]
)
class JiraCreationNotificationChannelIT : AbstractNotificationTestSupport() {

    @Autowired
    private lateinit var jiraCreationNotificationChannel: JiraCreationNotificationChannel

    @Autowired
    private lateinit var jiraConfigurationService: JIRAConfigurationService

    @Autowired
    private lateinit var mockRestTemplateProvider: MockRestTemplateProvider
    private lateinit var mockRestTemplateContext: MockRestTemplateContext

    @BeforeEach
    fun init() {
        mockRestTemplateContext = mockRestTemplateProvider.createSession()
    }

    @AfterEach
    fun close() {
        mockRestTemplateContext.close()
    }

    @Test
    fun `Creating a Jira ticket on a new promotion`() {
        asAdmin {

            // Jira project name
            val jiraProjectName = uid("J")

            // Jira configuration
            val configuration = JIRAFixtures.jiraConfiguration()
            withDisabledConfigurationTest {
                jiraConfigurationService.newConfiguration(
                    configuration
                )
            }

            project {
                branch {
                    val pl = promotionLevel()

                    // Setting up the Jira notification
                    eventSubscriptionService.subscribe(
                        channel = jiraCreationNotificationChannel,
                        channelConfig = JiraCreationNotificationChannelConfig(
                            configName = configuration.name,
                            projectName = jiraProjectName,
                            issueType = "Test",
                            labels = listOf("test"),
                            fixVersion = "v1",
                            assignee = "dcoraboeuf",
                            titleTemplate = "Build \${build} has been promoted to \${promotionLevel}",
                            customFields = mapOf(
                                "duedate" to TextNode("2024-04-16"),
                                "customfield_11000" to TextNode("Some direct value"),
                                "customfield_12000" to mapOf(
                                    "value" to "Some map value"
                                ).asJson(),
                            )
                        ),
                        projectEntity = pl,
                        keywords = null,
                        origin = "test",
                        contentTemplate = "<p>Build \${build} has been promoted to \${promotionLevel}.</p>",
                        EventFactory.NEW_PROMOTION_RUN,
                    )

                    val build = build()

                    // Mocking the call to Jira
                    mockRestTemplateContext.onPostJson(
                        uri = "http://jira/rest/api/2/issue",
                        body = mapOf(
                            "fields" to mapOf(
                                "project" to mapOf(
                                    "name" to jiraProjectName,
                                ),
                                "summary" to "Build ${build.name} has been promoted to ${pl.name}",
                                "issuetype" to mapOf(
                                    "name" to "Test"
                                ),
                                "labels" to listOf("test"),
                                "description" to """
                                    <p>Build <a href="http://localhost:8080/#/build/${build.id}">${build.name}</a> has been promoted to <a href="http://localhost:8080/#/promotionLevel/${pl.id}">${pl.name}</a>.</p>
                                """.trimIndent(),
                                "assignee" to mapOf(
                                    "name" to "dcoraboeuf"
                                ),
                                "fixVersions" to listOf(
                                    mapOf(
                                        "name" to "v1"
                                    )
                                ),
                                "duedate" to "2024-04-16",
                                "customfield_11000" to "Some direct value",
                                "customfield_12000" to mapOf(
                                    "value" to "Some map value"
                                ),
                            )
                        ),
                        outcome = success(
                            mapOf(
                                "key" to "$jiraProjectName-123",
                            )
                        )
                    )

                    build.promote(pl)

                    // Checks that the call has been done
                    mockRestTemplateContext.verify()

                    TODO("Checks the output of the notification (key + url)")
                }
            }
        }
    }

}