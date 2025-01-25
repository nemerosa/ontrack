package net.nemerosa.ontrack.extension.workflows.definition

/**
 * Result for the validation of a workflow
 */
data class WorkflowValidation(
    val name: String,
    val errors: List<String>,
) {
    fun throwErrorIfAny() {
        if (errors.isNotEmpty()) {
            val message = if (errors.size == 1) {
                errors.first()
            } else {
                errors.joinToString("\n") { "* $it" }
            }
            throw WorkflowValidationException(
                name = name,
                message = message
            )
        }
    }

    val error: Boolean = errors.isNotEmpty()

    companion object {

        fun validateWorkflow(workflow: Workflow): WorkflowValidation {
            // Name is required
            if (workflow.name.isBlank()) {
                return error(workflow.name, "Workflow name is required")
            }
            // One node required
            if (workflow.nodes.isEmpty()) {
                return error(workflow.name, "At least one node is required.")
            }
            // All parents must be known
            val nodeIds = workflow.nodes.map { it.id }.toSet()
            workflow.nodes.forEach { node ->
                node.parents.forEach { parent ->
                    if (parent.id !in nodeIds) {
                        return error(workflow.name, """Parent ID "${parent.id}" is not a valid node ID""")
                    }
                }
            }
            // Cycle detection
            if (isCyclic(workflow.nodes)) {
                return error(workflow.name, "The workflow contains at least one cycle")
            }
            // At least one starting node
            if (workflow.nodes.none { it.parents.isEmpty() }) {
                return error(workflow.name, "The workflow must have at least one starting node")
            }
            // OK
            return ok(name = workflow.name)
        }

        private fun isCyclic(nodes: List<WorkflowNode>): Boolean {
            val visited = mutableSetOf<String>()
            val recStack = mutableSetOf<String>()

            fun dfs(nodeId: String): Boolean {
                if (recStack.contains(nodeId)) return true
                if (visited.contains(nodeId)) return false

                visited.add(nodeId)
                recStack.add(nodeId)

                nodes.find { it.id == nodeId }?.parents?.forEach { parent ->
                    if (dfs(parent.id)) return true
                }

                recStack.remove(nodeId)
                return false
            }

            return nodes.any { dfs(it.id) }
        }

        fun error(name: String, message: String) = WorkflowValidation(
            name = name,
            errors = listOf(message),
        )

        fun error(name: String, ex: Exception) = error(
            name = name,
            message = ex.message ?: ex.javaClass.simpleName
        )

        fun unnamedError(ex: Exception) = error(
            name = "",
            message = ex.message ?: ex.javaClass.simpleName
        )

        fun ok(name: String) = WorkflowValidation(
            name = name,
            errors = emptyList(),
        )
    }
}
