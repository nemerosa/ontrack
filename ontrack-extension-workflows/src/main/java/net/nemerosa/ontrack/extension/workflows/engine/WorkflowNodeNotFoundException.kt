package net.nemerosa.ontrack.extension.workflows.engine

import net.nemerosa.ontrack.model.exceptions.NotFoundException

class WorkflowNodeNotFoundException(id: String) : NotFoundException(
    "Workflows node with id: $id was not found"
)
