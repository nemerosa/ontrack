package net.nemerosa.ontrack.extension.workflows.mock

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.TextNode
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.extension.workflows.WorkflowsExtensionFeature
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutor
import org.springframework.stereotype.Component

@Component
class MockWorkflowNodeExecutor(
    workflowsExtensionFeature: WorkflowsExtensionFeature,
) : AbstractExtension(workflowsExtensionFeature), WorkflowNodeExecutor {

    override val id: String = "mock"

    override fun execute(workflowInstance: WorkflowInstance, workflowNodeId: String): JsonNode {
        // Gets the node & its data
        val data = workflowInstance.workflow.getNode(workflowNodeId).data.asText()
        // Returning some new text
        return TextNode("Processed: $data")
    }
}