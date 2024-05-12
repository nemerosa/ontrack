package net.nemerosa.ontrack.extension.workflows.definition

/**
 * Result for the validation of a workflow
 */
data class WorkflowValidation(
    val errors: List<String>,
) {
    fun throwErrorIfAny() {
        if (errors.isNotEmpty()) {
            throw WorkflowValidationException(
                "Validation of the workflow returned the following errors:\n${errors.joinToString("\n") { "* $it" }}"
            )
        }
    }

    val error: Boolean = errors.isNotEmpty()

    companion object {

        fun error(message: String) = WorkflowValidation(
            errors = listOf(message),
        )

        fun error(ex: Exception) = error(
            ex.message ?: ex.javaClass.simpleName
        )

        fun ok() = WorkflowValidation(
            errors = emptyList(),
        )
    }
}
