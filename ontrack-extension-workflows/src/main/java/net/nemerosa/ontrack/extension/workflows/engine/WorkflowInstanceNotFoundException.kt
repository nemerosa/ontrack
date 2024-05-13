package net.nemerosa.ontrack.extension.workflows.engine

import net.nemerosa.ontrack.model.exceptions.NotFoundException

class WorkflowInstanceNotFoundException(id: String) : NotFoundException(
    "Workflows instance with id: $id was not found"
)
