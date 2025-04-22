package net.nemerosa.ontrack.extension.workflows.notifications

import net.nemerosa.ontrack.extension.notifications.channels.NotificationResultType
import net.nemerosa.ontrack.extension.workflows.AbstractWorkflowTestSupport
import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowNode
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowEngine
import net.nemerosa.ontrack.extension.workflows.engine.getWorkflowInstance
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.MockEventType
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@AsAdminTest
class WorkflowNotificationChannelIT : AbstractWorkflowTestSupport() {

    @Autowired
    private lateinit var workflowNotificationChannel: WorkflowNotificationChannel

    @Autowired
    private lateinit var workflowEngine: WorkflowEngine

    @Test
    fun `Workflow is renamed before being launched`() {

        val project = project { }
        val event = Event.of(MockEventType)
            .withProject(project)
            .build()

        val result = asAdmin {
            workflowNotificationChannel.publish(
                recordId = "1",
                config = WorkflowNotificationChannelConfig(
                    workflow = Workflow(
                        name = "For project ${'$'}{project}",
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
        }

        assertEquals(NotificationResultType.ASYNC, result.type)
        assertNotNull(result.output) { output ->
            val instanceId = output.workflowInstanceId
            val instance = workflowEngine.getWorkflowInstance(instanceId)
            assertEquals(
                "For project ${project.name}",
                instance.workflow.name,
            )
        }
    }

}