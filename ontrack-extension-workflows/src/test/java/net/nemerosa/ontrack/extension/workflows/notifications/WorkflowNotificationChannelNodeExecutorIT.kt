package net.nemerosa.ontrack.extension.workflows.notifications

import net.nemerosa.ontrack.extension.notifications.mock.MockNotificationChannel
import net.nemerosa.ontrack.extension.workflows.AbstractWorkflowTestSupport
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.templating.TestTemplatingContextData
import net.nemerosa.ontrack.model.templating.TestTemplatingContextHandler
import net.nemerosa.ontrack.model.templating.createTemplatingContextData
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class WorkflowNotificationChannelNodeExecutorIT : AbstractWorkflowTestSupport() {

    @Autowired
    private lateinit var mockNotificationChannel: MockNotificationChannel

    @Autowired
    private lateinit var eventFactory: EventFactory

    @Autowired
    private lateinit var testTemplatingContextHandler: TestTemplatingContextHandler

    @Test
    fun `Notifications in a workflow can reuse the output of the parent nodes in their templates`() {
        val targetTicket = uid("t-")
        val ticketData = uid("PRJ-")
        val targetMail = uid("m-")
        val yaml = """
            name: Notifications in a workflow can reuse the output of the parent nodes in their templates
            nodes:
                - id: ticket-creation
                  executorId: notification
                  data:
                    channel: mock
                    channelConfig:
                        target: $targetTicket
                        data: $ticketData
                    template: Creating the ticket
                - id: mail
                  executorId: notification
                  parents:
                    - id: ticket-creation
                  data:
                    channel: mock
                    channelConfig:
                        target: $targetMail
                    template: |
                        Ticket created: ${'$'}{workflow.ticket-creation?path=result.data}
        """.trimIndent()

        project {

            val event = eventFactory.newProject(this)

            // Registering the workflow, launching it & waiting for its completion
            workflowTestSupport.registerLaunchAndWaitForWorkflow(yaml, event = event, display = true)
            // Checks all messages have been recorded
            assertEquals(
                listOf("Creating the ticket"),
                mockNotificationChannel.targetMessages(targetTicket)
            )
            assertEquals(
                listOf("Ticket created: $ticketData"),
                mockNotificationChannel.targetMessages(targetMail)
            )

        }
    }

    @Test
    fun `Passing some contextual data to a workflow so that it can be used in a template`() {
        val target = uid("t-")
        val yaml = """
            name: Notification can use some contextual data for templating
            nodes:
                - id: test
                  executorId: notification
                  data:
                    channel: mock
                    channelConfig:
                        target: $target
                    template: |
                        Triggered ${'$'}{deployment.id} as ${'$'}{deployment.url}
        """.trimIndent()

        project {

            val event = eventFactory.newProject(this)

            // Registering the workflow, launching it & waiting for its completion
            workflowTestSupport.registerLaunchAndWaitForWorkflow(
                yaml = yaml,
                event = event,
                contexts = mapOf(
                    "deployment" to testTemplatingContextHandler.createTemplatingContextData(
                        TestTemplatingContextData(
                            id = "123",
                        )
                    )
                ),
                display = true
            )
            // Checks all messages have been recorded
            assertEquals(
                listOf(
                    """
                        Triggered 123 as mock://123
                    """.trimIndent()
                ),
                mockNotificationChannel.targetMessages(target)
            )
        }
    }

}