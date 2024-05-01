package net.nemerosa.ontrack.kdsl.acceptance.tests.workflows

import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
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
        // TODO Saving the workflow
//        ontrack.workflows.saveYamlWorkflow(
//            workflow = workflow,
//            executor = "mock",
//        )
        // TODO Running the workflow
        // TODO Waiting for the workflow result
    }

}