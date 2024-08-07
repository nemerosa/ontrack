package net.nemerosa.ontrack.extension.workflows.execution

import net.nemerosa.ontrack.model.exceptions.NotFoundException

class WorkflowNodeExecutorNotFoundException(id: String) : NotFoundException(
    "Workflow node executor $id not found"
)
