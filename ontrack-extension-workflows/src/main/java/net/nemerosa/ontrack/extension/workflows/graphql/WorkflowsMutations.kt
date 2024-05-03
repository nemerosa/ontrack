package net.nemerosa.ontrack.extension.workflows.graphql

import com.fasterxml.jackson.databind.JsonNode
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
            name = "saveYamlWorkflow",
            description = "Saves a workflow which is defined as YAML",
            input = SaveYamlWorkflowInput::class,
            outputName = "workflowId",
            outputDescription = "Saved workflow ID",
            outputType = String::class
        ) { input ->
            workflowRegistry.saveYamlWorkflow(input.workflow, input.executorId)
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

data class SaveYamlWorkflowInput(
    val workflow: String,
    val executorId: String,
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
