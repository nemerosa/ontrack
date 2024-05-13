package net.nemerosa.ontrack.extension.workflows.definition

import net.nemerosa.ontrack.model.exceptions.InputException

class WorkflowValidationException(
    message: String,
) : InputException(message)
