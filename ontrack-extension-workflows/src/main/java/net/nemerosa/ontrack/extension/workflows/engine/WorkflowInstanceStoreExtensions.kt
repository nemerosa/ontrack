package net.nemerosa.ontrack.extension.workflows.engine

fun WorkflowInstanceStore.getById(id: String) =
    findById(id) ?: throw WorkflowInstanceNotFoundException(id)
