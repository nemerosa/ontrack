package net.nemerosa.ontrack.extension.workflows.graphql

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowValidation
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowContext
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowContextData
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowEngine
import net.nemerosa.ontrack.extension.workflows.registry.WorkflowRegistry
import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.ListRef
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import org.springframework.stereotype.Component

@Component
class WorkflowsMutations(
    private val workflowRegistry: WorkflowRegistry,
    private val workflowEngine: WorkflowEngine,
) : TypedMutationProvider() {

    override val mutations: List<Mutation> = listOf(
        simpleMutation(
            name = "validateJsonWorkflow",
            description = "Validates a workflow which is defined as JSON",
            input = ValidateJsonWorkflowInput::class,
            outputName = "validation",
            outputDescription = "Result of the validation",
            outputType = WorkflowValidation::class
        ) { input ->
            workflowRegistry.validateJsonWorkflow(input.workflow)
        },
        simpleMutation(
            name = "saveYamlWorkflow",
            description = "Saves a workflow which is defined as YAML",
            input = SaveYamlWorkflowInput::class,
            outputName = "workflowId",
            outputDescription = "Saved workflow ID",
            outputType = String::class
        ) { input ->
            workflowRegistry.saveYamlWorkflow(input.workflow)
        },
        simpleMutation(
            name = "launchWorkflow",
            description = "Launches an existing workflow",
            input = LaunchWorkflowInput::class,
            outputName = "workflowInstanceId",
            outputDescription = "Workflow instance ID",
            outputType = String::class
        ) { input ->
            val workflowRecord = workflowRegistry.findWorkflow(input.workflowId)
            if (workflowRecord != null) {
                workflowEngine.startWorkflow(
                    workflow = workflowRecord.workflow,
                    context = WorkflowContext(
                        input.context.map {
                            WorkflowContextData(it.key, it.value)
                        }
                    ),
                ).id
            } else {
                null
            }
        },
    )
}

data class ValidateJsonWorkflowInput(
    val workflow: JsonNode,
)

data class SaveYamlWorkflowInput(
    val workflow: String,
)

data class LaunchWorkflowInput(
    val workflowId: String,
    @ListRef(embedded = true)
    val context: List<LaunchWorkflowInputContext>,
)

data class LaunchWorkflowInputContext(
    val key: String,
    val value: JsonNode,
)
