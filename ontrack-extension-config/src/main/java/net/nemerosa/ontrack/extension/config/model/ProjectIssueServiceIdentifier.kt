package net.nemerosa.ontrack.extension.config.model

data class ProjectIssueServiceIdentifier(
    val serviceId: String,
    val serviceName: String,
) {
    fun toRepresentation() = "$serviceId//$serviceName"
}
