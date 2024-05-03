package net.nemerosa.ontrack.extension.workflows.engine

import com.fasterxml.jackson.databind.node.TextNode
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowFixtures
import net.nemerosa.ontrack.extension.workflows.mock.MockWorkflowNodeExecutor
import net.nemerosa.ontrack.extension.workflows.registry.WorkflowRegistry
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.waitUntil
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertEquals
import kotlin.test.fail
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@TestPropertySource(
    properties = [
        "net.nemerosa.ontrack.extension.workflows.store=memory",
        "ontrack.extension.queue.general.async=false",
    ]
)
class WorkflowEngineIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var workflowEngine: WorkflowEngine

    @Autowired
    private lateinit var workflowRegistry: WorkflowRegistry

    @Autowired
    private lateinit var mockWorkflowNodeExecutor: MockWorkflowNodeExecutor

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
        // Registering the workflow
        val workflowId = workflowRegistry.saveYamlWorkflow(yaml, "mock")
        // Getting the workflow
        val record = workflowRegistry.findWorkflow(workflowId) ?: fail("No workflow found for $workflowId")
        // Launching the workflow
        val instance = workflowEngine.startWorkflow(record.workflow, WorkflowContext("mock", TextNode("Complex")))
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
        // Registering the workflow
        val workflowId = workflowRegistry.saveYamlWorkflow(yaml, "mock")
        // Getting the workflow
        val record = workflowRegistry.findWorkflow(workflowId) ?: fail("No workflow found for $workflowId")
        // Launching the workflow
        val instance = workflowEngine.startWorkflow(record.workflow, WorkflowContext("mock", TextNode("Complex")))
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
                "Processed: Starting for Complex",
                "Processed: Parallel A for Complex",
                "Processed: Parallel B for Complex",
                "Processed: End of (Processed: Parallel A for Complex) and (Processed: Parallel B for Complex) for Complex",
            ),
            texts.toSet()
        )
    }

    @Test
    fun `Asymetric workflow with reusing parent outputs`() {
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
        // Registering the workflow
        val workflowId = workflowRegistry.saveYamlWorkflow(yaml, "mock")
        // Getting the workflow
        val record = workflowRegistry.findWorkflow(workflowId) ?: fail("No workflow found for $workflowId")
        // Launching the workflow
        val instance = workflowEngine.startWorkflow(record.workflow, WorkflowContext("mock", TextNode("Complex")))
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
                "Processed: Starting for Complex",
                "Processed: Parallel A1 for Complex",
                "Processed: Parallel A2 for Complex",
                "Processed: Parallel B for Complex",
                "Processed: End for Complex",
            ),
            texts.toSet()
        )
    }

}