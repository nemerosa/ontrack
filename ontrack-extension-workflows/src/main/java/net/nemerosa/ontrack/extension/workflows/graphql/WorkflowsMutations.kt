package net.nemerosa.ontrack.extension.workflows.graphql

import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import org.springframework.stereotype.Component

@Component
class WorkflowsMutations: TypedMutationProvider() {

    override val mutations: List<Mutation> = listOf(
        simpleMutation(
            name = "saveYamlWorkflow",
            description = "Saves a workflow which is defined as YAML",
            input = SaveYamlWorkflowInput::class,
            outputName = "workflowId",
            outputDescription = "Saved workflow ID",
            outputType = String::class
        ) { input ->
            TODO("Validates and saves the workflow, returning an ID")
        },
        simpleMutation(
            name = "launchWorkflow",
            description = "Launches an existing workflow",
            input = LaunchWorkflowInput::class,
            outputName = "workflowInstanceId",
            outputDescription = "Workflow instance ID",
            outputType = String::class
        ) { input ->
            TODO("Launches the workflow")
        },
    )
}

data class SaveYamlWorkflowInput(
    val workflow: String,
    val executorId: String,
)

data class LaunchWorkflowInput(
    val workflowId: String,
    // TODO Context
)
