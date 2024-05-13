package net.nemerosa.ontrack.extension.workflows.engine

import com.fasterxml.jackson.databind.node.TextNode
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WorkflowInstanceNodeTest {

    @Test
    fun `Success node`() {
        val node = WorkflowInstanceNode(
            id = "node",
            status = WorkflowInstanceNodeStatus.STARTED,
            output = null,
            error = null,
        )
        val next = node.success(TextNode("test"))
        assertEquals(node.id, next.id)
        assertEquals(WorkflowInstanceNodeStatus.SUCCESS, next.status)
        assertEquals("test", next.output?.asText())
    }

}