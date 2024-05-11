package net.nemerosa.ontrack.extension.workflows.registry

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowValidation

interface WorkflowRegistry {

    fun validateJsonWorkflow(workflow: JsonNode): WorkflowValidation

    fun saveYamlWorkflow(workflow: String): String

    fun findWorkflow(workflowId: String): WorkflowRecord?

}