package net.nemerosa.ontrack.extension.workflows.engine

import com.fasterxml.jackson.databind.node.TextNode
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.workflows.registry.WorkflowParser
import net.nemerosa.ontrack.model.events.MockEventType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WorkflowInstanceTest {

    @Test
    fun `Success node`() {
        val workflowInstance = WorkflowInstanceFixtures.simpleLinear()
        val next = workflowInstance.successNode("start", TextNode("Processing"))
        assertEquals(workflowInstance.id, next.id)
        assertEquals(workflowInstance.workflow, next.workflow)
        val output = next.nodesExecutions.find { it.id == "start" }?.output
        assertEquals("Processing", output?.asText())
    }

    @Test
    fun `One error node marks the instance as errored`() {
        val workflowInstance = WorkflowInstance(
            id = "test",
            timestamp = Time.now,
            workflow = testWorkflow(),
            event = MockEventType.serializedMockEvent("Sample"),
            status = WorkflowInstanceStatus.ERROR,
            nodesExecutions = listOf(
                WorkflowInstanceNode(
                    id = "ticket",
                    status = WorkflowInstanceNodeStatus.SUCCESS,
                    startTime = Time.now,
                    endTime = Time.now,
                    output = null,
                    error = null,
                ),
                WorkflowInstanceNode(
                    id = "jenkins",
                    status = WorkflowInstanceNodeStatus.ERROR,
                    startTime = Time.now,
                    endTime = Time.now,
                    output = null,
                    error = "Timeout",
                ),
                WorkflowInstanceNode(
                    id = "mail",
                    status = WorkflowInstanceNodeStatus.CANCELLED,
                    startTime = Time.now,
                    endTime = Time.now,
                    output = null,
                    error = "Cancelled",
                ),
            )
        )
        assertEquals(
            WorkflowInstanceStatus.ERROR,
            workflowInstance.computeStatus()
        )
    }

    @Test
    fun `One created node marks the instance as running`() {
        val workflowInstance = testInstance(WorkflowInstanceNodeStatus.CREATED)
        assertEquals(
            WorkflowInstanceStatus.RUNNING,
            workflowInstance.status
        )
    }

    @Test
    fun `One started node marks the instance as running`() {
        val workflowInstance = testInstance(WorkflowInstanceNodeStatus.STARTED)
        assertEquals(
            WorkflowInstanceStatus.RUNNING,
            workflowInstance.status
        )
    }

    @Test
    fun `One waiting node marks the instance as running`() {
        val workflowInstance = testInstance(WorkflowInstanceNodeStatus.WAITING)
        assertEquals(
            WorkflowInstanceStatus.RUNNING,
            workflowInstance.status
        )
    }

    private fun testInstance(lastNodeStatus: WorkflowInstanceNodeStatus): WorkflowInstance {
        val nodesExecutions = listOf(
            WorkflowInstanceNode(
                id = "ticket",
                status = WorkflowInstanceNodeStatus.SUCCESS,
                startTime = Time.now,
                endTime = Time.now,
                output = null,
                error = null,
            ),
            WorkflowInstanceNode(
                id = "jenkins",
                status = WorkflowInstanceNodeStatus.ERROR,
                startTime = Time.now,
                endTime = Time.now,
                output = null,
                error = "Timeout",
            ),
            WorkflowInstanceNode(
                id = "mail",
                status = lastNodeStatus,
                startTime = Time.now,
                endTime = Time.now,
                output = null,
                error = "Cancelled",
            ),
        )
        return WorkflowInstance(
            id = "test",
            timestamp = Time.now,
            workflow = testWorkflow(),
            event = MockEventType.serializedMockEvent("Sample"),
            status = WorkflowInstance.computeStatus(nodesExecutions),
            nodesExecutions = nodesExecutions
        )
    }

    private fun testWorkflow() = WorkflowParser.parseYamlWorkflow(
        """
                        name: Test
                        nodes:
                          - id: ticket
                            executorId: mock
                            data:
                              text: Ticket
                          - id: jenkins
                            parents:
                              - id: ticket
                            executorId: mock
                            data:
                              text: Jenkins
                          - id: mail
                            parents:
                              - id: jenkins
                            executorId: mock
                            data:
                              text: Mail
                    """.trimIndent(),
    )

}