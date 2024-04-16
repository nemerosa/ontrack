package net.nemerosa.ontrack.extension.jira.notifications

import com.fasterxml.jackson.databind.node.TextNode
import net.nemerosa.ontrack.extension.jira.JIRAConfigurationService
import net.nemerosa.ontrack.extension.jira.JIRAFixtures
import net.nemerosa.ontrack.extension.jira.mock.MockJIRAInstance
import net.nemerosa.ontrack.extension.notifications.AbstractNotificationTestSupport
import net.nemerosa.ontrack.extension.notifications.mail.AbstractMailTestSupport
import net.nemerosa.ontrack.extension.notifications.mail.MailNotificationChannelConfig
import net.nemerosa.ontrack.extension.notifications.subscriptions.subscribe
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
    properties = [
        "ontrack.config.extension.jira.mock.enabled=true",
    ]
)
class JiraCreationNotificationChannelIT : AbstractNotificationTestSupport() {

    @Autowired
    private lateinit var jiraCreationNotificationChannel: JiraCreationNotificationChannel

    @Autowired
    private lateinit var mockJIRAInstance: MockJIRAInstance

    @Autowired
    private lateinit var jiraConfigurationService: JIRAConfigurationService

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
                            titleTemplate = "Build \${build} has been promoted to \${pl.name}.",
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
                        contentTemplate = "Build \${build} has been promoted to \${pl.name}.",
                        EventFactory.NEW_PROMOTION_RUN,
                    )

                    build {
                        promote(pl)

                        // TODO Checking the Jira mock client
                    }
                }
            }
        }
    }

}