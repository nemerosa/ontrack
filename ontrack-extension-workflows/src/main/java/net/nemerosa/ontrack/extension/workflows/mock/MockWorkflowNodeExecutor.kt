package net.nemerosa.ontrack.extension.workflows.mock

import com.fasterxml.jackson.databind.JsonNode
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.extension.workflows.WorkflowsExtensionFeature
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutor
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
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
        val nodeRawData = workflowInstance.workflow.getNode(workflowNodeId).data
        val nodeData = if (nodeRawData.isTextual) {
            MockNodeData(nodeRawData.asText())
        } else {
            nodeRawData.parse<MockNodeData>()
        }
        // Using the context
        val execContext = workflowInstance.context
        val context = if (execContext.has("text")) {
            execContext.path("text").asText()
        } else {
            execContext.asText()
        }
        // Returning some new text
        val text = "Processed: ${nodeData.text} for $context"
        // Waiting time
        if (nodeData.waitMs > 0) {
            runBlocking {
                delay(nodeData.waitMs)
            }
        }
        // Recording
        val old = texts[workflowInstance.id]
        texts[workflowInstance.id] = if (old != null) old + text else listOf(text)
        // OK
        return MockNodeOutput(
            text = text
        ).asJson()
    }
}