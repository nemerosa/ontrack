package net.nemerosa.ontrack.kdsl.acceptance.tests.workflows

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.spec.extension.workflows.WorkflowInstanceStatus
import net.nemerosa.ontrack.kdsl.spec.extension.workflows.mock.mock
import net.nemerosa.ontrack.kdsl.spec.extension.workflows.workflows
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ACCDSLWorkflows : AbstractACCDSLTestSupport() {

    @Test
    fun `Simple linear workflow`() {
        // Defining a workflow
        val name = uid("w-")
        val workflow = """
            name: $name
            nodes:
                - id: start
                  executorId: mock
                  data: "Start node"
                - id: end
                  executorId: mock
                  data: "End node"
                  parents:
                    - id: start
        """.trimIndent()
        // Saving the workflow
        val workflowId = ontrack.workflows.saveYamlWorkflow(
            workflow = workflow,
            executor = "mock",
        ) ?: fail("Error while saving workflow")
        // Running the workflow
        val instanceId = ontrack.workflows.launchWorkflow(
            workflowId = workflowId,
            context = "mock" to mapOf("text" to "Linear").asJson(),
        ) ?: fail("Error while launching workflow")
        // Waiting for the workflow result
        waitUntilWorkflowFinished(instanceId)
        // Checks the outcome of the workflow run
        val texts = ontrack.workflows.mock.getTexts(instanceId)
        assertEquals(
            listOf(
                "Processed: Start node for Linear",
                "Processed: End node for Linear",
            ),
            texts
        )
    }

    @Test
    fun `Parallel with join workflow`() {
        // Defining a workflow
        val name = uid("w-")
        val workflow = """
            name: $name
            nodes:
                - id: start-a
                  executorId: mock
                  data: "Start node A"
                - id: start-b
                  executorId: mock
                  data: "Start node B"
                - id: end
                  executorId: mock
                  data: "End node"
                  parents:
                    - id: start-a
                    - id: start-b
        """.trimIndent()
        // Saving the workflow
        val workflowId = ontrack.workflows.saveYamlWorkflow(
            workflow = workflow,
            executor = "mock",
        ) ?: fail("Error while saving workflow")
        // Running the workflow
        val instanceId = ontrack.workflows.launchWorkflow(
            workflowId = workflowId,
            context = "mock" to mapOf("text" to "Parallel / Join").asJson(),
        ) ?: fail("Error while launching workflow")
        // Waiting for the workflow result
        waitUntilWorkflowFinished(instanceId)
        // Checks the outcome of the workflow run
        val texts = ontrack.workflows.mock.getTexts(instanceId)
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
        // Defining a workflow
        val name = uid("w-")
        val workflow = """
            name: $name
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
        // Saving the workflow
        val workflowId = ontrack.workflows.saveYamlWorkflow(
            workflow = workflow,
            executor = "mock",
        ) ?: fail("Error while saving workflow")
        // Running the workflow
        val instanceId = ontrack.workflows.launchWorkflow(
            workflowId = workflowId,
            context = "mock" to mapOf("text" to "Complex").asJson(),
        ) ?: fail("Error while launching workflow")
        // Waiting for the workflow result
        waitUntilWorkflowFinished(instanceId)
        // Checks the outcome of the workflow run
        val texts = ontrack.workflows.mock.getTexts(instanceId)
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

    private fun waitUntilWorkflowFinished(instanceId: String) {
        waitUntil(
            timeout = 30_000L,
            interval = 500L,
        ) {
            val instance = ontrack.workflows.workflowInstance(instanceId)
            instance != null && instance.finished
        }
        // Getting the final errors
        val instance = ontrack.workflows.workflowInstance(instanceId)
            ?: fail("Could not get the workflow instance")
        if (instance.status == WorkflowInstanceStatus.ERROR) {
            // Displaying the errors
            instance.nodesExecutions.forEach { node ->
                println("Node: ${node.id}")
                println("  Status: ${node.status}")
                println("  Output: ${node.output}")
                println("  Error: ${node.error}")
            }
            // Failing
            fail("Workflow failed in error. See errors above.")
        }
    }

}