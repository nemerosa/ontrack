package net.nemerosa.ontrack.extension.config.model

import com.fasterxml.jackson.annotation.JsonIgnore
import net.nemerosa.ontrack.extension.config.extensions.CIConfigExtensionService
import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Project configuration")
data class ProjectConfiguration(
    override val properties: List<PropertyConfiguration> = emptyList(),
    override val extensions: List<ExtensionConfiguration> = emptyList(),
    val projectName: String? = null,
    val scmConfig: String? = null,
    val issueServiceIdentifier: ProjectIssueServiceIdentifier? = null,
    val scmIndexationInterval: Int? = null,
) : PropertiesConfiguration, ExtensionsConfiguration {

    @JsonIgnore
    fun isNotEmpty(): Boolean = properties.isNotEmpty() ||
            !projectName.isNullOrBlank() ||
            !scmConfig.isNullOrBlank() ||
            issueServiceIdentifier != null ||
            scmIndexationInterval != null ||
            extensions.isNotEmpty()


    fun merge(project: ProjectConfiguration, ciConfigExtensionService: CIConfigExtensionService) = ProjectConfiguration(
        properties = this.properties + project.properties,
        projectName = project.projectName ?: this.projectName,
        issueServiceIdentifier = project.issueServiceIdentifier ?: this.issueServiceIdentifier,
        scmIndexationInterval = project.scmIndexationInterval ?: this.scmIndexationInterval,
        extensions = ciConfigExtensionService.merge(this.extensions, project.extensions),
    )
}
