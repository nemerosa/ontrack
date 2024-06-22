package net.nemerosa.ontrack.extension.workflows.notifications

import com.fasterxml.jackson.databind.node.NullNode
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.nemerosa.ontrack.extension.notifications.queue.NotificationQueueItem
import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowNode
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowEngine
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventTemplatingService
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test

class WorkflowNotificationChannelTest {

    @Test
    fun `Workflow is renamed before being launched`() {

        val workflowEngine = mockk<WorkflowEngine>()
        val workflowNotificationItemConverter = mockk<WorkflowNotificationItemConverter>()
        val eventTemplatingService = mockk<EventTemplatingService>()

        val channel = WorkflowNotificationChannel(
            workflowEngine = workflowEngine,
            workflowNotificationItemConverter = workflowNotificationItemConverter,
            eventTemplatingService = eventTemplatingService,
        )

        val event = mockk<Event>()
        val notificationQueueItem = NotificationQueueItem(
            source = null,
            channel = "workflow",
            channelConfig = NullNode.instance,
            eventType = "test",
            signature = null,
            entities = emptyMap(),
            extraEntities = emptyMap(),
            ref = null,
            values = emptyMap(),
            template = null,
        )

        val instanceId = uid("wi-")

        every {
            workflowNotificationItemConverter.convertForQueue(event, instanceId)
        } returns notificationQueueItem

        every {
            eventTemplatingService.renderEvent(event, emptyMap(), "Initial name", PlainEventRenderer.INSTANCE)
        } returns "New name"

        val instance = mockk<WorkflowInstance>()

        every {
            instance.id
        } returns "1"

        every {
            workflowEngine.startWorkflow(any(), any(), any())
        } returns instance

        channel.publish(
            config = WorkflowNotificationChannelConfig(
                workflow = Workflow(
                    name = "Initial name",
                    nodes = listOf(
                        WorkflowNode(
                            id = "start",
                            executorId = "mock",
                            data = mapOf("text" to "Test").asJson(),
                            parents = emptyList()
                        )
                    )
                )
            ),
            event = event,
            context = emptyMap(),
            template = null,
            outputProgressCallback = { it },
        )

        verify {
            workflowEngine.startWorkflow(
                workflow = Workflow(
                    name = "New name",
                    nodes = listOf(
                        WorkflowNode(
                            id = "start",
                            executorId = "mock",
                            data = mapOf("text" to "Test").asJson(),
                            parents = emptyList()
                        )
                    )
                ),
                context = any(),
                contextContribution = any(),
            )
        }
    }

}