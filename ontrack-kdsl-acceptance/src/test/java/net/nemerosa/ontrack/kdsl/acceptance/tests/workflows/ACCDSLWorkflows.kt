package net.nemerosa.ontrack.kdsl.acceptance.tests.workflows

import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import org.junit.jupiter.api.Test

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
        ).id
        // Running the workflow
        val instanceId = ontrack.workflows.launchWorkflow(
            workflowId = workflowId,
            // TODO Context
        )
        // Waiting for the workflow result
        waitUntil {
            val instance = ontrack.workflows.workflowInstance(instanceId)
            instance != null && instance.finished
        }
        // TODO Checks the outcome of the workflow run
    }

}