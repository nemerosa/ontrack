package net.nemerosa.ontrack.extension.workflows.templating

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstanceNode
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstanceNodeStatus
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WorkflowTemplatingRenderableTest {

    @Test
    fun `Accessing a path from another node output`() {

        val node = mockk<WorkflowInstanceNode>()
        every { node.output } returns mapOf(
            "result" to mapOf(
                "data" to "Sample"
            )
        ).asJson()
        every { node.status } returns WorkflowInstanceNodeStatus.SUCCESS

        val instance = mockk<WorkflowInstance>()
        every { instance.getNode("ticket-creation") } returns node

        val renderable = WorkflowTemplatingRenderable(instance)

        // workflow.ticket-creation?path=data
        val text = renderable.render(
            field = "ticket-creation",
            configMap = mapOf("path" to "result.data"),
            renderer = PlainEventRenderer.INSTANCE
        )

        assertEquals("Sample", text)
    }

}