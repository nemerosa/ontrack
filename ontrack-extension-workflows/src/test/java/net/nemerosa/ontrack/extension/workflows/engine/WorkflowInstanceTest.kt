package net.nemerosa.ontrack.extension.workflows.engine

import com.fasterxml.jackson.databind.node.TextNode
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WorkflowInstanceTest {

    @Test
    fun `Success node`() {
        val workflowInstance = WorkflowInstanceFixtures.simpleLinear()
        val next = workflowInstance.successNode("start", TextNode("Processing"))
        assertEquals(workflowInstance.id, next.id)
        assertEquals(workflowInstance.workflow, next.workflow)
        assertEquals(workflowInstance.executorId, next.executorId)
        val output = next.nodesExecutions.find { it.id == "start" }?.output
        assertEquals("Processing", output?.asText())
    }

}