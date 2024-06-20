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
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.docs.DocumentationExampleCode
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile(RunProfile.ACC, RunProfile.UNIT_TEST, RunProfile.DEV)
@APIDescription("Executor used to mock some actions for the nodes. Mostly used for testing.")
@Documentation(MockNodeData::class)
@Documentation(MockNodeOutput::class, section = "output")
@DocumentationExampleCode("""
    executorId: mock
    data:
        text: Some text to store
        waitMs: 500
        error: false
""")
class MockWorkflowNodeExecutor(
    workflowsExtensionFeature: WorkflowsExtensionFeature,
) : AbstractExtension(workflowsExtensionFeature), WorkflowNodeExecutor {

    override val id: String = "mock"
    override val displayName: String = "Mock"

    private val texts = mutableMapOf<String, List<String>>()

    fun getTextsByInstanceId(instanceId: String): List<String> = texts[instanceId] ?: emptyList()

    override suspend fun execute(workflowInstance: WorkflowInstance, workflowNodeId: String): JsonNode {
        // Gets the node & its data
        val nodeRawData = workflowInstance.workflow.getNode(workflowNodeId).data
        val nodeData = if (nodeRawData.isTextual) {
            MockNodeData(nodeRawData.asText())
        } else {
            nodeRawData.parse<MockNodeData>()
        }
        // Error?
        if (nodeData.error) {
            error("Error in $workflowNodeId node")
        }
        // Waiting time
        if (nodeData.waitMs > 0) {
            runBlocking {
                delay(nodeData.waitMs)
            }
        }
        // Gets the parent outputs in an index
        val parentsData = workflowInstance.getParentsData(workflowNodeId)
        // Using the context
        val execContext = workflowInstance.context.getValue("mock")
        val context = if (execContext.has("text")) {
            execContext.path("text").asText()
        } else {
            execContext.asText()
        }
        // Initial text
        val initialText = nodeData.text
        // Replacements by parents references
        val replacedText = "#([a-zA-Z][a-zA-Z0-9_-]*)".toRegex().replace(initialText) { m ->
            val parentId = m.groupValues[1]
            val parentOutput = parentsData[parentId]
            if (parentOutput != null) {
                val output = parentOutput.parse<MockNodeOutput>()
                output.text
            } else {
                // Parent has no data
                "#none"
            }
        }
        // Returning some new text
        val text = "Processed: $replacedText for $context"
        // Recording
        val old = texts[workflowInstance.id]
        texts[workflowInstance.id] = if (old != null) old + text else listOf(text)
        // OK
        return MockNodeOutput(
            text = text
        ).asJson()
    }
}