package net.nemerosa.ontrack.extension.workflows.engine

import com.fasterxml.jackson.databind.node.TextNode
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowFixtures
import net.nemerosa.ontrack.extension.workflows.mock.MockWorkflowNodeExecutor
import net.nemerosa.ontrack.extension.workflows.AbstractWorkflowTestSupport
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowValidationException
import net.nemerosa.ontrack.it.waitUntil
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

class WorkflowEngineIT : AbstractWorkflowTestSupport() {

    @Autowired
    private lateinit var workflowEngine: WorkflowEngine

    @Autowired
    private lateinit var mockWorkflowNodeExecutor: MockWorkflowNodeExecutor

    @OptIn(ExperimentalTime::class)
    @Test
    fun `Simple linear workflow`() {
        // Defining a workflow
        val workflow = WorkflowFixtures.simpleLinearWorkflow()
        // Running the workflow
        val instance = workflowEngine.startWorkflow(workflow, WorkflowContext("mock", TextNode("Linear")))
        // Waiting until the workflow is completed (error or success)
        waitUntil("Waiting until workflow is complete", timeout = 10.seconds) {
            val workflowInstance = workflowEngine.getWorkflowInstance(instance.id)
            println("workflowInstance = $workflowInstance")
            workflowInstance.status.finished
        }
        // Checks the results
        val texts = mockWorkflowNodeExecutor.getTextsByInstanceId(instance.id)
        assertEquals(
            listOf(
                "Processed: Start node for Linear",
                "Processed: End node for Linear",
            ),
            texts
        )
    }

    @Test
    fun `Launching a workflow with an error`() {
        // Defining a workflow
        val workflow = WorkflowFixtures.cyclicWorkflow()
        // Running the workflow
        assertFailsWith<WorkflowValidationException> {
            workflowEngine.startWorkflow(workflow, WorkflowContext("mock", TextNode("Cyclic")))
        }
    }

    @Test
    fun `Workflow stopped when node in error`() {
        // Defining a workflow using YAML
        val yaml = """
            name: Workflow in error
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
                    waitMs: 2000
                    error: true
                - id: mail
                  executorId: mock
                  data:
                    text: Mail
                    waitMs: 500
                  parents:
                    - id: jenkins
        """.trimIndent()
        // Registering the workflow, launching it & waiting for its completion
        val instanceId = workflowTestSupport.registerLaunchAndWaitForWorkflow(yaml, "NodeErrorTest")
        // Checks the results
        val texts = mockWorkflowNodeExecutor.getTextsByInstanceId(instanceId)
        assertEquals(
            setOf(
                "Processed: Ticket for NodeErrorTest",
            ),
            texts.toSet()
        )
        // Checking the status of the workflow
        val instance = workflowEngine.getWorkflowInstance(instanceId)
        assertEquals(WorkflowInstanceStatus.ERROR, instance.status)
        // Checking all nodes
        assertEquals(WorkflowInstanceNodeStatus.SUCCESS, instance.getNode("ticket").status)
        assertEquals(WorkflowInstanceNodeStatus.ERROR, instance.getNode("jenkins").status)
        assertEquals(WorkflowInstanceNodeStatus.STOPPED, instance.getNode("mail").status)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `Parallel with join`() {
        // Defining a workflow
        val workflow = WorkflowFixtures.twoParallelAndJoin()
        // Running the workflow
        val instance = workflowEngine.startWorkflow(workflow, WorkflowContext("mock", TextNode("Parallel / Join")))
        // Waiting until the workflow is completed (error or success)
        waitUntil("Waiting until workflow is complete", timeout = 10.seconds) {
            val workflowInstance = workflowEngine.getWorkflowInstance(instance.id)
            println("workflowInstance = $workflowInstance")
            workflowInstance.status.finished
        }
        // Checks the results
        val texts = mockWorkflowNodeExecutor.getTextsByInstanceId(instance.id)
        assertEquals(
            setOf(
                "Processed: Start node A for Parallel / Join",
                "Processed: Start node B for Parallel / Join",
                "Processed: End node for Parallel / Join",
            ),
            texts.toSet()
        )
    }

    @Test
    fun `Complex workflow with waiting times`() {
        // Defining a workflow using YAML
        val yaml = """
            name: Complex workflow with waiting times
            nodes:
                - id: start
                  executorId: mock
                  data:
                    text: Starting
                    waitMs: 500
                - id: parallel-a
                  executorId: mock
                  data:
                    text: Parallel A
                    waitMs: 500
                  parents:
                    - id: start
                - id: parallel-b
                  executorId: mock
                  data:
                    text: Parallel B
                    waitMs: 2000
                  parents:
                    - id: start
                - id: end
                  executorId: mock
                  data:
                    text: End
                  parents:
                    - id: parallel-a
                    - id: parallel-b
        """.trimIndent()
        // Registering the workflow, launching it & waiting for its completion
        val instanceId = workflowTestSupport.registerLaunchAndWaitForWorkflow(yaml, "Complex")
        // Checks the results
        val texts = mockWorkflowNodeExecutor.getTextsByInstanceId(instanceId)
        assertEquals(
            setOf(
                "Processed: Starting for Complex",
                "Processed: Parallel A for Complex",
                "Processed: Parallel B for Complex",
                "Processed: End for Complex",
            ),
            texts.toSet()
        )
    }

    @Test
    fun `Complex workflow with reusing parent outputs`() {
        // Defining a workflow using YAML
        val yaml = """
            name: Complex workflow with waiting times
            nodes:
                - id: start
                  executorId: mock
                  data:
                    text: Starting
                    waitMs: 500
                - id: parallel-a
                  executorId: mock
                  data:
                    text: Parallel A
                    waitMs: 500
                  parents:
                    - id: start
                - id: parallel-b
                  executorId: mock
                  data:
                    text: Parallel B
                    waitMs: 2000
                  parents:
                    - id: start
                - id: end
                  executorId: mock
                  data:
                    text: End of (#parallel-a) and (#parallel-b)
                  parents:
                    - id: parallel-a
                    - id: parallel-b
        """.trimIndent()
        // Registering the workflow, launching it & waiting for its completion
        val instanceId = workflowTestSupport.registerLaunchAndWaitForWorkflow(yaml, "Complex")
        // Checks the results
        val texts = mockWorkflowNodeExecutor.getTextsByInstanceId(instanceId)
        assertEquals(
            setOf(
                "Processed: Starting for Complex",
                "Processed: Parallel A for Complex",
                "Processed: Parallel B for Complex",
                "Processed: End of (Processed: Parallel A for Complex) and (Processed: Parallel B for Complex) for Complex",
            ),
            texts.toSet()
        )
    }

    @Test
    fun `Asymetric workflow`() {
        // Defining a workflow using YAML
        val yaml = """
            name: Complex workflow with waiting times
            nodes:
                - id: start
                  executorId: mock
                  data:
                    text: Starting
                    waitMs: 500
                - id: parallel-a-1
                  executorId: mock
                  data:
                    text: Parallel A1
                    waitMs: 1000
                  parents:
                    - id: start
                - id: parallel-a-2
                  executorId: mock
                  data:
                    text: Parallel A2
                    waitMs: 1000
                  parents:
                    - id: parallel-a-1
                - id: parallel-b
                  executorId: mock
                  data:
                    text: Parallel B
                    waitMs: 500
                  parents:
                    - id: start
                - id: end
                  executorId: mock
                  data:
                    text: End
                  parents:
                    - id: parallel-a-2
                    - id: parallel-b
        """.trimIndent()
        // Registering the workflow, launching it & waiting for its completion
        val instanceId = workflowTestSupport.registerLaunchAndWaitForWorkflow(yaml, "Complex")
        // Checks the results
        val texts = mockWorkflowNodeExecutor.getTextsByInstanceId(instanceId)
        assertEquals(
            setOf(
                "Processed: Starting for Complex",
                "Processed: Parallel A1 for Complex",
                "Processed: Parallel A2 for Complex",
                "Processed: Parallel B for Complex",
                "Processed: End for Complex",
            ),
            texts.toSet()
        )
    }

    /**
     * Testing a workflow looking like:
     *
     * ```
     * s
     * |\
     * | p
     * |/
     * e
     * ```
     */
    @Test
    fun `SPE workflow`() {
        // Defining a workflow using YAML
        val yaml = """
            name: SPE workflow
            nodes:
                - id: start
                  executorId: mock
                  data:
                    text: Start
                - id: parallel
                  executorId: mock
                  data:
                    text: Parallel
                  parents:
                    - id: start
                - id: end
                  executorId: mock
                  data:
                    text: End
                  parents:
                    - id: start
                    - id: parallel
        """.trimIndent()
        // Registering the workflow, launching it & waiting for its completion
        val instanceId = workflowTestSupport.registerLaunchAndWaitForWorkflow(yaml, "SPE")
        // Checks the results
        val texts = mockWorkflowNodeExecutor.getTextsByInstanceId(instanceId)
        assertEquals(
            setOf(
                "Processed: Start for SPE",
                "Processed: Parallel for SPE",
                "Processed: End for SPE",
            ),
            texts.toSet()
        )
    }

}