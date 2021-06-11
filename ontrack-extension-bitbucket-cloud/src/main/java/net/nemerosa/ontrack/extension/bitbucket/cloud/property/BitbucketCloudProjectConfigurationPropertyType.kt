package net.nemerosa.ontrack.extension.bitbucket.cloud.property

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.MapBuilder
import net.nemerosa.ontrack.extension.bitbucket.cloud.BitbucketCloudExtensionFeature
import net.nemerosa.ontrack.extension.bitbucket.cloud.configuration.BitbucketCloudConfiguration
import net.nemerosa.ontrack.extension.bitbucket.cloud.configuration.BitbucketCloudConfigurationService
import net.nemerosa.ontrack.extension.git.property.AbstractGitProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Form.Companion.create
import net.nemerosa.ontrack.model.form.Int
import net.nemerosa.ontrack.model.form.Selection
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.security.ProjectConfig
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.support.ConfigurationPropertyType
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.Function

@Component
class BitbucketCloudProjectConfigurationPropertyType(
    extensionFeature: BitbucketCloudExtensionFeature,
    private val configurationService: BitbucketCloudConfigurationService,
    private val issueServiceRegistry: IssueServiceRegistry
) : AbstractGitProjectConfigurationPropertyType<BitbucketCloudProjectConfigurationProperty>(extensionFeature),
    ConfigurationPropertyType<BitbucketCloudConfiguration, BitbucketCloudProjectConfigurationProperty> {

    override fun getName(): String = "Bitbucket Cloud configuration"

    override fun getDescription(): String = "Associates the project with a Bitbucket Cloud repository"

    override fun getSupportedEntityTypes(): Set<ProjectEntityType> = EnumSet.of(ProjectEntityType.PROJECT)

    override fun canEdit(entity: ProjectEntity, securityService: SecurityService): Boolean =
        securityService.isProjectFunctionGranted(entity.projectId(), ProjectConfig::class.java)

    override fun canView(entity: ProjectEntity, securityService: SecurityService): Boolean = true

    override fun getEditionForm(entity: ProjectEntity, value: BitbucketCloudProjectConfigurationProperty?): Form {
        val availableIssueServiceConfigurations = issueServiceRegistry.availableIssueServiceConfigurations
        return create()
            .with(
                Selection.of("configuration")
                    .label("Configuration")
                    .help("Bitbucket Cloud configuration to use to access the repository")
                    .items(configurationService.configurationDescriptors)
                    .value(value?.configuration?.name)
            )
            .with(
                Text.of("repository")
                    .label("Repository")
                    .help("Slug of the repository in Bitbucket Cloud")
                    .value(value?.repository)
            )
            .with(
                Int.of("indexationInterval")
                    .label("Indexation interval")
                    .min(0)
                    .max(60 * 24)
                    .value(value?.indexationInterval ?: 0)
                    .help("@file:extension/git/help.net.nemerosa.ontrack.extension.git.model.GitConfiguration.indexationInterval.tpl.html")
            )
            .with(
                Selection.of("issueServiceConfigurationIdentifier")
                    .label("Issue configuration")
                    .help("Select an issue service that is sued to associate tickets and issues to the source.")
                    .optional()
                    .items(availableIssueServiceConfigurations)
                    .value(value?.issueServiceConfigurationIdentifier ?: "")
            )
    }

    override fun fromClient(node: JsonNode): BitbucketCloudProjectConfigurationProperty = fromStorage(node)

    override fun fromStorage(node: JsonNode): BitbucketCloudProjectConfigurationProperty {
        val configurationName = node.path("configuration").asText()
        // Looks the configuration up
        val configuration: BitbucketCloudConfiguration = configurationService.getConfiguration(configurationName)
        // OK
        return BitbucketCloudProjectConfigurationProperty(
            configuration = configuration,
            repository = node.path("repository").asText(),
            indexationInterval = node.path("indexationInterval").asInt(),
            issueServiceConfigurationIdentifier = JsonUtils.get(node, "issueServiceConfigurationIdentifier", null)
        )
    }

    override fun forStorage(value: BitbucketCloudProjectConfigurationProperty): JsonNode {
        return format(
            MapBuilder.params()
                .with("configuration", value.configuration.name)
                .with("repository", value.repository)
                .with("indexationInterval", value.indexationInterval)
                .with("issueServiceConfigurationIdentifier", value.issueServiceConfigurationIdentifier)
                .get()
        )
    }

    override fun replaceValue(
        value: BitbucketCloudProjectConfigurationProperty,
        replacementFunction: Function<String, String>
    ): BitbucketCloudProjectConfigurationProperty = BitbucketCloudProjectConfigurationProperty(
        configurationService.replaceConfiguration(value.getConfiguration(), replacementFunction),
        replacementFunction.apply(value.repository),
        value.indexationInterval,
        value.issueServiceConfigurationIdentifier
    )

}