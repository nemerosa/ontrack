package net.nemerosa.ontrack.extension.workflows.engine

import net.nemerosa.ontrack.model.exceptions.NotFoundException

class WorkflowNodeExecutorNotFoundException(id: String) : NotFoundException(
    "Workflow node executor $id not found"
)
