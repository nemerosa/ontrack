package net.nemerosa.ontrack.extension.workflows.engine

fun WorkflowEngine.getWorkflowInstance(id: String): WorkflowInstance =
    findWorkflowInstance(id)
        ?: throw WorkflowInstanceNotFoundException(id)
