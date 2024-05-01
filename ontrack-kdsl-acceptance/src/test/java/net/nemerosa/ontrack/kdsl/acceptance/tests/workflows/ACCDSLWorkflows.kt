package net.nemerosa.ontrack.kdsl.acceptance.tests.workflows

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
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
            data: ""
            nodes:
                - id: start
                  data: "Start node"
                - id: end
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
            context = mapOf("text" to "Linear").asJson(),
        ) ?: fail("Error while launching workflow")
        // Waiting for the workflow result
        waitUntil {
            val instance = ontrack.workflows.workflowInstance(instanceId)
            instance != null && instance.finished
        }
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
            data: ""
            nodes:
                - id: start-a
                  data: "Start node A"
                - id: start-b
                  data: "Start node B"
                - id: end
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
            context = mapOf("text" to "Parallel / Join").asJson(),
        ) ?: fail("Error while launching workflow")
        // Waiting for the workflow result
        waitUntil {
            val instance = ontrack.workflows.workflowInstance(instanceId)
            instance != null && instance.finished
        }
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

}