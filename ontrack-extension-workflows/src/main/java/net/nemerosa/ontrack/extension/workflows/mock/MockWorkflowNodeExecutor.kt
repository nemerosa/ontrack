package net.nemerosa.ontrack.extension.workflows.mock

import com.fasterxml.jackson.databind.JsonNode
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.extension.workflows.WorkflowsExtensionFeature
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutor
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutorConfigException
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutorResult
import net.nemerosa.ontrack.extension.workflows.templating.WorkflowTemplatingContext
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.docs.DocumentationExampleCode
import net.nemerosa.ontrack.model.events.EventTemplatingService
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import net.nemerosa.ontrack.model.events.SerializableEventService
import net.nemerosa.ontrack.model.templating.TemplatingService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile(RunProfile.DEV)
@APIDescription("Executor used to mock some actions for the nodes. Mostly used for testing.")
@Documentation(MockNodeData::class)
@Documentation(MockNodeOutput::class, section = "output")
@DocumentationExampleCode(
    """
    executorId: mock
    data:
        text: Some text to store
        waitMs: 500
        error: false
"""
)
class MockWorkflowNodeExecutor(
    workflowsExtensionFeature: WorkflowsExtensionFeature,
    private val eventTemplatingService: EventTemplatingService,
    private val serializableEventService: SerializableEventService,
    private val templatingService: TemplatingService,
) : AbstractExtension(workflowsExtensionFeature), WorkflowNodeExecutor {

    companion object {
        const val EVENT_MOCK = "mock"
    }

    override val id: String = "mock"
    override val displayName: String = "Mock"

    private val texts = mutableMapOf<String, List<String>>()

    fun getTextsByInstanceId(instanceId: String): List<String> = texts[instanceId] ?: emptyList()

    private fun parseData(data: JsonNode): MockNodeData =
        if (data.isTextual) {
            MockNodeData(data.asText())
        } else {
            data.parse<MockNodeData>()
        }

    override fun validate(data: JsonNode) {
        val parsed = parseData(data)
        if (parsed.text.isBlank()) {
            throw WorkflowNodeExecutorConfigException("Text is required for mock node executor")
        }
    }

    override fun execute(
        workflowInstance: WorkflowInstance,
        workflowNodeId: String,
        workflowNodeExecutorResultFeedback: (output: JsonNode?) -> Unit,
    ): WorkflowNodeExecutorResult {
        // Gets the node & its data
        val nodeRawData = workflowInstance.workflow.getNode(workflowNodeId).data
        val nodeData = parseData(nodeRawData)
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

        // Using the event context
        val context = workflowInstance.event.findValue(EVENT_MOCK)

        // Initial text
        val initialText = nodeData.text

        // Templating
        val replacedText = if (templatingService.isTemplate(initialText)) {
            val templatingEvent = serializableEventService.hydrate(workflowInstance.event)
            val additionalContext = WorkflowTemplatingContext.createTemplatingContext(workflowInstance)
            eventTemplatingService.renderEvent(
                event = templatingEvent,
                template = initialText,
                renderer = PlainEventRenderer.INSTANCE,
                context = additionalContext,
            )
        } else {
            initialText
        }

        // Returning some new text
        val text = "Processed: $replacedText for $context"

        // Recording
        val old = texts[workflowInstance.id]
        texts[workflowInstance.id] = if (old != null) old + text else listOf(text)
        // OK
        return WorkflowNodeExecutorResult.success(
            MockNodeOutput(
                text = text
            ).asJson()
        )
    }
}