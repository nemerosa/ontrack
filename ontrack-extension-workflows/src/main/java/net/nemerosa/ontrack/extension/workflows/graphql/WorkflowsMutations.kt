package net.nemerosa.ontrack.extension.workflows.graphql

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.workflows.acl.WorkflowStop
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowValidation
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowEngine
import net.nemerosa.ontrack.extension.workflows.events.WorkflowEventFactory
import net.nemerosa.ontrack.extension.workflows.registry.WorkflowRegistry
import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.events.dehydrate
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Component

@Component
class WorkflowsMutations(
    private val workflowRegistry: WorkflowRegistry,
    private val workflowEngine: WorkflowEngine,
    private val securityService: SecurityService,
    private val workflowEventFactory: WorkflowEventFactory,
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
                    event = workflowEventFactory.workflowStandalone().dehydrate(),
                ).id
            } else {
                null
            }
        },
        unitMutation(
            name = "stopWorkflow",
            description = "Stopping a running workflow. Does not do anything if already stopped.",
            input = StopWorkflowInput::class,
        ) { input ->
            securityService.checkGlobalFunction(WorkflowStop::class.java)
            workflowEngine.stopWorkflow(input.workflowInstanceId)
        }
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
)

data class StopWorkflowInput(
    val workflowInstanceId: String,
)
