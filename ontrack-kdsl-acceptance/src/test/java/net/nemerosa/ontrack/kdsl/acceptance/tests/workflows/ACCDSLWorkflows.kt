package net.nemerosa.ontrack.kdsl.acceptance.tests.workflows

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.spec.extension.workflows.WorkflowInstanceNodeStatus
import net.nemerosa.ontrack.kdsl.spec.extension.workflows.WorkflowInstanceStatus
import net.nemerosa.ontrack.kdsl.spec.extension.workflows.mock.mock
import net.nemerosa.ontrack.kdsl.spec.extension.workflows.workflows
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
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
                  data:
                    text: "Start node"
                - id: end
                  executorId: mock
                  data:
                    text: "End node"
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
            context = mapOf("mock" to "Linear"),
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
                  data:
                    text: "Start node A"
                - id: start-b
                  executorId: mock
                  data:
                    text: "Start node B"
                - id: end
                  executorId: mock
                  data:
                    text: "End node"
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
            context = mapOf("mock" to "Parallel / Join"),
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
            context = mapOf("mock" to "Complex"),
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
            context = mapOf("mock" to "Complex"),
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
            context = mapOf("mock" to "SPE"),
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
            context = mapOf("mock" to "Error test"),
        ) ?: fail("Error while launching workflow")
        // Waiting for the workflow result
        val instance = waitUntilWorkflowFinished(instanceId, returnInstanceOnError = true)
        assertEquals(WorkflowInstanceStatus.ERROR, instance.status)
        // Checks the node statuses
        val nodeStatuses = instance.nodesExecutions.associate { it.id to it.status }
        assertEquals(WorkflowInstanceNodeStatus.SUCCESS, nodeStatuses["ticket"])
        assertEquals(WorkflowInstanceNodeStatus.ERROR, nodeStatuses["jenkins"])
        assertEquals(WorkflowInstanceNodeStatus.CANCELLED, nodeStatuses["mail"])
    }

    @Test
    fun `Workflow stopped in case of timeout`() {
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
                  timeout: 1
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
            context = mapOf("mock" to "Error test"),
        ) ?: fail("Error while launching workflow")
        // Waiting for the workflow result
        val instance = waitUntilWorkflowFinished(instanceId, returnInstanceOnError = true)
        assertEquals(WorkflowInstanceStatus.ERROR, instance.status)
        // Checks the node statuses
        val nodeStatuses = instance.nodesExecutions.associate { it.id to it.status }
        assertEquals(WorkflowInstanceNodeStatus.SUCCESS, nodeStatuses["ticket"])
        assertEquals(WorkflowInstanceNodeStatus.ERROR, nodeStatuses["jenkins"])
        assertEquals(WorkflowInstanceNodeStatus.CANCELLED, nodeStatuses["mail"])
    }

    @Test
    fun `Stopping a workflow`() {
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
                  timeout: 1
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
            context = mapOf("mock" to "Error test"),
        ) ?: fail("Error while launching workflow")
        // Stopping the workflow
        runBlocking {
            delay(500)
            ontrack.workflows.stopWorkflow(instanceId)
        }
        // Waiting for the workflow result
        val instance = waitUntilWorkflowFinished(instanceId, returnInstanceOnError = true)
        assertEquals(WorkflowInstanceStatus.STOPPED, instance.status)
        // Checks the node statuses
        val nodeStatuses = instance.nodesExecutions.associate { it.id to it.status }
        assertEquals(WorkflowInstanceNodeStatus.SUCCESS, nodeStatuses["ticket"])
        assertEquals(WorkflowInstanceNodeStatus.CANCELLED, nodeStatuses["jenkins"])
        assertEquals(WorkflowInstanceNodeStatus.CANCELLED, nodeStatuses["mail"])
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
            context = mapOf("mock" to "Error test"),
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

    /**
     * To test the saturation, we need to limit the parallelism
     */
    @Test
    fun `Workflow number of executors in parallel`() {
        val name = uid("w-")
        // Tries to saturate the server by launching 20 nodes
        val nodes = mutableListOf(
            mapOf(
                "id" to "jenkins-0",
                "executorId" to "mock",
                "timeout" to 5, // seconds
                "data" to mapOf(
                    "text" to "Jenkins 0",
                    "waitMs" to 2000,
                )
            )
        )
        (1..20).forEach {
            nodes.add(
                mapOf(
                    "id" to "jenkins-$it",
                    "parents" to listOf(
                        mapOf(
                            "id" to "jenkins-${it - 1}"
                        )
                    ),
                    "executorId" to "mock",
                    "timeout" to 5, // seconds
                    "data" to mapOf(
                        "text" to "Jenkins $it",
                        "waitMs" to 2000,
                    )
                )
            )
        }
        // Node that does not depend on any other
        nodes.add(
            mapOf(
                "id" to "email",
                "executorId" to "mock",
                "timeout" to 5, // seconds
                "data" to mapOf(
                    "text" to "Email",
                )
            )
        )
        val workflow = mutableMapOf(
            "name" to name,
            "nodes" to nodes
        )
        // Saving the workflow
        val workflowId = ontrack.workflows.saveJsonWorkflow(
            workflow = workflow.asJson(),
        ) ?: fail("Error while saving workflow")
        // Launches two workflow instances in parallel
        runBlocking {
            val jobs = (1..2).map {
                async {
                    // Running the workflow
                    val instanceId = ontrack.workflows.launchWorkflow(
                        workflowId = workflowId,
                        context = mapOf("mock" to "Heavy load $it"),
                    ) ?: fail("Error while launching workflow")
                    // Waiting for the workflow result
                    waitUntilWorkflowFinished(
                        instanceId,
                        timeout = 120_000L,
                        returnInstanceOnError = true
                    )
                }
            }
            val instances = jobs.awaitAll()
            assertTrue(
                instances.all {
                    it.status == WorkflowInstanceStatus.SUCCESS
                },
                "All instances are successful"
            )
        }
    }

}