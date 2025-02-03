package net.nemerosa.ontrack.extension.workflows.definition

import net.nemerosa.ontrack.extension.workflows.registry.WorkflowParser
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WorkflowNodeExtensionsTest {

    @Test
    fun `Single node default timeout`() {
        val workflow = WorkflowParser.parseYamlWorkflow(
            """
                name: test
                nodes:
                    - id: start
                      executorId: mock
                      data: {}
            """.trimIndent()
        )
        val node = workflow.getNode("start")
        assertEquals(
            300,
            node.totalTimeout(workflow)
        )
    }

    @Test
    fun `Single node custom timeout`() {
        val workflow = WorkflowParser.parseYamlWorkflow(
            """
                name: test
                nodes:
                    - id: start
                      executorId: mock
                      timeout: 1800
                      data: {}
            """.trimIndent()
        )
        val node = workflow.getNode("start")
        assertEquals(
            1800,
            node.totalTimeout(workflow)
        )
    }

    @Test
    fun `Single node line timeout`() {
        val workflow = WorkflowParser.parseYamlWorkflow(
            """
                name: test
                nodes:
                    - id: start
                      executorId: mock
                      timeout: 1800
                      data: {}
                    - id: middle
                      parents:
                        - id: start
                      executorId: mock
                      timeout: 900
                      data: {}
                    - id: end
                      parents:
                        - id: middle
                      executorId: mock
                      timeout: 450
                      data: {}
            """.trimIndent()
        )
        val node = workflow.getNode("end")
        assertEquals(
            3150,
            node.totalTimeout(workflow)
        )
    }

    @Test
    fun `Timeout on the longest branch`() {
        val workflow = WorkflowParser.parseYamlWorkflow(
            """
                name: test
                nodes:
                    - id: email
                      executorId: mock
                      timeout: 100
                      data: {}
                    - id: line-1
                      executorId: mock
                      timeout: 800
                      data: {}
                    - id: line-2
                      parents:
                        - id: line-1
                      executorId: mock
                      timeout: 400
                      data: {}
                    - id: end
                      parents:
                        - id: line-2
                        - id: email
                      executorId: mock
                      timeout: 200
                      data: {}
            """.trimIndent()
        )
        val node = workflow.getNode("end")
        assertEquals(
            1400,
            node.totalTimeout(workflow)
        )
    }

}