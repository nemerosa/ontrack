package net.nemerosa.ontrack.extension.workflows.definition

import net.nemerosa.ontrack.model.exceptions.InputException

class WorkflowValidationException(
    name: String,
    message: String,
) : InputException(
    if (name.isBlank()) {
        """
            Validation of the workflow returned the following error > $message
        """.trimIndent()
    } else {
        """
            Validation of the "$name" workflow returned the following error > $message
        """.trimIndent()
    }
)
