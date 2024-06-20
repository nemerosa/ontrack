package net.nemerosa.ontrack.kdsl.acceptance.tests.workflows

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.spec.extension.workflows.WorkflowInstanceNodeStatus
import net.nemerosa.ontrack.kdsl.spec.extension.workflows.WorkflowInstanceStatus
import net.nemerosa.ontrack.kdsl.spec.extension.workflows.mock.mock
import net.nemerosa.ontrack.kdsl.spec.extension.workflows.workflows
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ACCDSLWorkflows : AbstractACCDSLWorkflowsTestSupport() {

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

    @Test
    fun `Asymetric workflow`() {
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
                - id: parallel-a-1
                  executorId: mock
                  data:
                    text: Parallel A1
                    waitMs: 1000
                  parents:
                    - id: start
                - id: parallel-b
                  executorId: mock
                  data:
                    text: Parallel B
                    waitMs: 500
                  parents:
                    - id: start
                - id: parallel-a-2
                  executorId: mock
                  data:
                    text: Parallel A2
                    waitMs: 1000
                  parents:
                    - id: parallel-a-1
                - id: end
                  executorId: mock
                  data:
                    text: End
                  parents:
                    - id: parallel-a-2
                    - id: parallel-b
        """.trimIndent()
        // Saving the workflow
        val workflowId = ontrack.workflows.saveYamlWorkflow(
            workflow = workflow,
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
                "Processed: Parallel A1 for Complex",
                "Processed: Parallel A2 for Complex",
                "Processed: Parallel B for Complex",
                "Processed: End for Complex",
            ),
            texts.toSet()
        )
    }

    @Test
    fun `SPE workflow`() {
        // Defining a workflow
        val name = uid("w-")
        val workflow = """
            name: SPE $name workflow
            nodes:
                - id: start
                  executorId: mock
                  data:
                    text: Start
                - id: end
                  executorId: mock
                  data:
                    text: End
                  parents:
                    - id: start
                    - id: parallel
                - id: parallel
                  executorId: mock
                  data:
                    text: Parallel
                  parents:
                    - id: start
        """.trimIndent()
        // Saving the workflow
        val workflowId = ontrack.workflows.saveYamlWorkflow(
            workflow = workflow,
        ) ?: fail("Error while saving workflow")
        // Running the workflow
        val instanceId = ontrack.workflows.launchWorkflow(
            workflowId = workflowId,
            context = "mock" to mapOf("text" to "SPE").asJson(),
        ) ?: fail("Error while launching workflow")
        // Waiting for the workflow result
        waitUntilWorkflowFinished(instanceId)
        // Checks the outcome of the workflow run
        val texts = ontrack.workflows.mock.getTexts(instanceId)
        assertEquals(
            setOf(
                "Processed: Start for SPE",
                "Processed: Parallel for SPE",
                "Processed: End for SPE",
            ),
            texts.toSet()
        )
    }

    @Test
    fun `Workflow stopped in case of error`() {
        val name = uid("w-")
        val workflow = """
            name: $name
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
        // Saving the workflow
        val workflowId = ontrack.workflows.saveYamlWorkflow(
            workflow = workflow,
        ) ?: fail("Error while saving workflow")
        // Running the workflow
        val instanceId = ontrack.workflows.launchWorkflow(
            workflowId = workflowId,
            context = "mock" to mapOf("text" to "Error test").asJson(),
        ) ?: fail("Error while launching workflow")
        // Waiting for the workflow result
        val instance = waitUntilWorkflowFinished(instanceId, returnInstanceOnError = true)
        assertEquals(WorkflowInstanceStatus.ERROR, instance.status)
        // Checks the node statuses
        val nodeStatuses = instance.nodesExecutions.associate { it.id to it.status }
        assertEquals(WorkflowInstanceNodeStatus.SUCCESS, nodeStatuses["ticket"])
        assertEquals(WorkflowInstanceNodeStatus.ERROR, nodeStatuses["jenkins"])
        assertEquals(WorkflowInstanceNodeStatus.STOPPED, nodeStatuses["mail"])
    }

    @Test
    fun `Error in workflow`() {

        // Defining a workflow
        val name = uid("w-")
        val workflow = """
            name: $name
            nodes:
                - id: start
                  executorId: mock
                  data:
                    text: Start
                - id: end
                  executorId: mock
                  data:
                    text: End
                    error: true
                  parents:
                    - id: start
        """.trimIndent()
        // Saving the workflow
        val workflowId = ontrack.workflows.saveYamlWorkflow(
            workflow = workflow,
        ) ?: fail("Error while saving workflow")
        // Running the workflow
        val instanceId = ontrack.workflows.launchWorkflow(
            workflowId = workflowId,
            context = "mock" to mapOf("text" to "Error test").asJson(),
        ) ?: fail("Error while launching workflow")
        // Waiting for the workflow result
        val instance = waitUntilWorkflowFinished(instanceId, returnInstanceOnError = true)
        // Checks the errors
        val nodeInError = instance.nodesExecutions.find { it.id == "end" }
            ?: fail("Cannot find the end node")
        // Checking the error
        assertEquals(
            WorkflowInstanceNodeStatus.ERROR,
            nodeInError.status
        )
        assertEquals(
            "Error in end node",
            nodeInError.error
        )
    }

}