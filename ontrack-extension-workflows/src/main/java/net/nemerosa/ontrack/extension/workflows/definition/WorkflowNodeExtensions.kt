package net.nemerosa.ontrack.extension.workflows.definition

fun WorkflowNode.totalTimeout(workflow: Workflow): Long {
    val ownTimeout = timeout
    val parentTimeout = parents.maxOfOrNull { parent ->
        workflow.getNode(parent.id).totalTimeout(workflow)
    } ?: 0
    return ownTimeout + parentTimeout
}