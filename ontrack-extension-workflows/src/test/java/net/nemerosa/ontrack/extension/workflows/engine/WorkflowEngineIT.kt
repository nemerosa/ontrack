package net.nemerosa.ontrack.extension.workflows.engine

import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.TextNode
import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowNode
import net.nemerosa.ontrack.extension.workflows.mock.MockWorkflowNodeExecutor
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.waitUntil
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class WorkflowEngineIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var workflowEngine: WorkflowEngine

    @Autowired
    private lateinit var mockWorkflowNodeExecutor: MockWorkflowNodeExecutor

    @Test
    fun `Simple linear workflow`() {
        // Defining a workflow
        val workflow = Workflow(
            name = "Test workflow",
            data = NullNode.instance,
            nodes = listOf(
                WorkflowNode(
                    id = "start",
                    data = TextNode("Start node"),
                    parents = emptyList(),
                ),
                WorkflowNode(
                    id = "end",
                    data = TextNode("End node"),
                    parents = listOf("start"),
                ),
            )
        )
        // Running the workflow
        val instance = workflowEngine.startWorkflow(workflow, mockWorkflowNodeExecutor)
        // Waiting until the workflow is completed (error or success)
        waitUntil("Waiting until workflow is complete") {
            workflowEngine.getWorkflowInstance(instance.id).status.finished
        }
    }

}