package net.nemerosa.ontrack.extension.git.property

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.MapBuilder
import net.nemerosa.ontrack.extension.git.GitExtensionFeature
import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration
import net.nemerosa.ontrack.extension.git.service.GitConfigurationService
import net.nemerosa.ontrack.model.security.ProjectConfig
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.support.ConfigurationPropertyType
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.Function


@Component
@Deprecated("Will be removed in V5. Pure Git configuration won't be supported any longer.")
class GitProjectConfigurationPropertyType(
    extensionFeature: GitExtensionFeature,
    private val configurationService: GitConfigurationService
) : AbstractGitProjectConfigurationPropertyType<GitProjectConfigurationProperty>(extensionFeature),
    ConfigurationPropertyType<BasicGitConfiguration, GitProjectConfigurationProperty> {

    override fun getName(): String = "Git configuration"

    override fun getDescription(): String = "Associates the project with a Git repository"

    override fun getSupportedEntityTypes(): Set<ProjectEntityType> = EnumSet.of(ProjectEntityType.PROJECT)

    override fun canEdit(entity: ProjectEntity, securityService: SecurityService): Boolean {
        return securityService.isProjectFunctionGranted(entity.projectId(), ProjectConfig::class.java)
    }

    override fun canView(entity: ProjectEntity, securityService: SecurityService): Boolean {
        return true
    }

    override fun fromClient(node: JsonNode): GitProjectConfigurationProperty {
        return fromStorage(node)
    }

    override fun fromStorage(node: JsonNode): GitProjectConfigurationProperty {
        val configurationName = node.path("configuration").asText()
        // Looks the configuration up
        val configuration = configurationService.getConfiguration(configurationName)
        // OK
        return GitProjectConfigurationProperty(
            configuration
        )
    }

    override fun forStorage(value: GitProjectConfigurationProperty): JsonNode {
        return format(
            MapBuilder.params()
                .with("configuration", value.configuration.name)
                .get()
        )
    }

    override fun replaceValue(
        value: GitProjectConfigurationProperty,
        replacementFunction: Function<String, String>
    ): GitProjectConfigurationProperty {
        return GitProjectConfigurationProperty(
            configurationService.replaceConfiguration(value.configuration, replacementFunction)
        )
    }
}
