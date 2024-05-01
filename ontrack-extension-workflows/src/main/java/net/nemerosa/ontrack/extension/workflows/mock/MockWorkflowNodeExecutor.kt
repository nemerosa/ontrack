package net.nemerosa.ontrack.extension.workflows.mock

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.TextNode
import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.extension.workflows.WorkflowsExtensionFeature
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutor
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile(RunProfile.ACC, RunProfile.UNIT_TEST, RunProfile.DEV)
class MockWorkflowNodeExecutor(
    workflowsExtensionFeature: WorkflowsExtensionFeature,
) : AbstractExtension(workflowsExtensionFeature), WorkflowNodeExecutor {

    override val id: String = "mock"

    private val texts = mutableMapOf<String, List<String>>()

    fun getTextsByInstanceId(instanceId: String): List<String> = texts[instanceId] ?: emptyList()

    override fun execute(workflowInstance: WorkflowInstance, workflowNodeId: String): JsonNode {
        // Gets the node & its data
        val data = workflowInstance.workflow.getNode(workflowNodeId).data.asText()
        // Using the context
        val execContext = workflowInstance.context
        val context = if (execContext.has("text")) {
            execContext.path("text").asText()
        } else {
            execContext.asText()
        }
        // Returning some new text
        val text = "Processed: $data for $context"
        val old = texts[workflowInstance.id]
        texts[workflowInstance.id] = if (old != null) old + text else listOf(text)
        return TextNode(text)
    }
}