package net.nemerosa.ontrack.extension.workflows.notifications

import net.nemerosa.ontrack.extension.notifications.mock.MockNotificationChannel
import net.nemerosa.ontrack.extension.workflows.AbstractWorkflowTestSupport
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class WorkflowNotificationChannelNodeExecutorIT : AbstractWorkflowTestSupport() {

    @Autowired
    private lateinit var mockNotificationChannel: MockNotificationChannel

    @Autowired
    private lateinit var eventFactory: EventFactory

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
                        Ticket created: ${'$'}{workflow.ticket-creation?path=data}
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

}