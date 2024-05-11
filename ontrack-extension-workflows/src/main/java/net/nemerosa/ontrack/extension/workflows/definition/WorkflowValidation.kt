package net.nemerosa.ontrack.extension.workflows.definition

/**
 * Result for the validation of a workflow
 */
data class WorkflowValidation(
    val errors: List<String>,
) {
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
