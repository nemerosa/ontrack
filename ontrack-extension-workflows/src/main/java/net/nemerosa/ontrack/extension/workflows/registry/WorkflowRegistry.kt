package net.nemerosa.ontrack.extension.workflows.registry

interface WorkflowRegistry {

    fun saveYamlWorkflow(workflow: String): String

    fun findWorkflow(workflowId: String): WorkflowRecord?

}