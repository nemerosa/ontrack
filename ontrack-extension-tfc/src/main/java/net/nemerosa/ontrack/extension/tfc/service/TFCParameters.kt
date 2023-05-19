package net.nemerosa.ontrack.extension.tfc.service

/**
 * Parameters used to identify a build validation.
 */
data class TFCParameters(
        val project: String,
        val branch: String?,
        val build: String?,
        val promotion: String?,
        val validation: String,
) {
    fun hasVariables(): Boolean =
            isVar(project) ||
                    isVar(branch) ||
                    isVar(build) ||
                    isVar(promotion) ||
                    isVar(validation)

    fun expand(variables: Map<String, String>, workspaceId: String) = TFCParameters(
            project = expand(project, variables, workspaceId),
            branch = branch?.let { expand(branch, variables, workspaceId) },
            build = build?.let { expand(build, variables, workspaceId) },
            promotion = promotion?.let { expand(promotion, variables, workspaceId) },
            validation = expand(validation, variables, workspaceId),
    )

    companion object {
        private fun isVar(value: String?): Boolean = value != null && value.startsWith("@")

        private fun expand(
                value: String,
                variables: Map<String, String>,
                workspaceId: String
        ): String =
                if (value.startsWith("@")) {
                    val varName = value.substringAfter("@")
                    variables[varName] ?: throw TFCMissingVariableException(varName, workspaceId)
                } else {
                    value
                }
    }

}