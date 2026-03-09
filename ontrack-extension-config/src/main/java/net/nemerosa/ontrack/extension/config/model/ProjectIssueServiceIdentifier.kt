package net.nemerosa.ontrack.extension.config.model

data class ProjectIssueServiceIdentifier(
    val serviceId: String,
    val serviceName: String,
) {
    fun toRepresentation() = "$serviceId//$serviceName"

    companion object {
        fun parse(value: String): ProjectIssueServiceIdentifier? =
            value.split("//").takeIf { it.size == 2 }?.let {
                ProjectIssueServiceIdentifier(it[0], it[1])
            }
    }
}
