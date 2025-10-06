package net.nemerosa.ontrack.extension.config.model

import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Project configuration")
data class ProjectConfiguration(
    override val properties: List<PropertyConfiguration> = emptyList(),
    val issueServiceIdentifier: ProjectIssueServiceIdentifier? = null,
) : PropertiesConfiguration {
    fun isNotEmpty(): Boolean = properties.isNotEmpty()
    fun merge(project: ProjectConfiguration) = ProjectConfiguration(
        properties = this.properties + project.properties,
        issueServiceIdentifier = project.issueServiceIdentifier ?: this.issueServiceIdentifier,
    )
}
