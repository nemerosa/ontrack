package net.nemerosa.ontrack.extension.git.property

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.git.GitBranchSearchIndexer
import net.nemerosa.ontrack.extension.git.GitBranchSearchItem
import net.nemerosa.ontrack.extension.git.GitExtensionFeature
import net.nemerosa.ontrack.extension.git.model.BuildGitCommitLink
import net.nemerosa.ontrack.extension.git.model.ConfiguredBuildGitCommitLink
import net.nemerosa.ontrack.extension.git.model.IndexableBuildGitCommitLink
import net.nemerosa.ontrack.extension.git.service.BuildGitCommitLinkService
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.extension.support.AbstractPropertyType
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.security.ProjectConfig
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.Function

@Component
class GitBranchConfigurationPropertyType(
        extensionFeature: GitExtensionFeature,
        private val buildGitCommitLinkService: BuildGitCommitLinkService,
        private val gitService: GitService,
        private val searchIndexService: SearchIndexService,
        private val gitBranchSearchIndexer: GitBranchSearchIndexer
) : AbstractPropertyType<GitBranchConfigurationProperty>(extensionFeature) {

    override fun getName(): String = "Git branch"

    override fun getDescription(): String = "Git branch"

    override fun getSupportedEntityTypes(): Set<ProjectEntityType> = EnumSet.of(ProjectEntityType.BRANCH)

    override fun canEdit(entity: ProjectEntity, securityService: SecurityService): Boolean {
        return securityService.isProjectFunctionGranted(entity.projectId(), ProjectConfig::class.java)
    }

    override fun canView(entity: ProjectEntity, securityService: SecurityService): Boolean = true

    override fun fromClient(node: JsonNode): GitBranchConfigurationProperty {
        return fromStorage(node)
    }

    override fun fromStorage(node: JsonNode): GitBranchConfigurationProperty {
        val configuredBuildGitCommitLink: ConfiguredBuildGitCommitLink<*>?
        configuredBuildGitCommitLink = if (node.has("buildCommitLink")) {
            val linkNode = node["buildCommitLink"]
            parseBuildCommitLink<Any>(linkNode)
        } else {
            null
        }
        val indexationAvailable = configuredBuildGitCommitLink != null && configuredBuildGitCommitLink.link is IndexableBuildGitCommitLink<*>
        return GitBranchConfigurationProperty(
                JsonUtils.get(node, "branch", "main"),
                configuredBuildGitCommitLink?.toServiceConfiguration(),
                indexationAvailable && JsonUtils.getBoolean(node, "override", false),
                if (indexationAvailable) JsonUtils.getInt(node, "buildTagInterval", 0) else 0
        )
    }

    private fun <T> parseBuildCommitLink(linkNode: JsonNode): ConfiguredBuildGitCommitLink<T>? {
        if (linkNode.isNull) {
            return null
        }
        val linkId = JsonUtils.get(linkNode, "id")
        // Gets the link data
        val linkDataNode = linkNode["data"]
        // Gets the link
        @Suppress("UNCHECKED_CAST")
        val link = buildGitCommitLinkService.getLink(linkId) as BuildGitCommitLink<T>
        // Parses the data (for validation)
        val linkData = link.parseData(linkDataNode)
        // OK
        return ConfiguredBuildGitCommitLink(
                link,
                linkData
        )
    }

    override fun replaceValue(value: GitBranchConfigurationProperty, replacementFunction: Function<String, String>): GitBranchConfigurationProperty {
        return GitBranchConfigurationProperty(
                replacementFunction.apply(value.branch),
                if (value.buildCommitLink != null) replaceBuildCommitLink<Any>(value.buildCommitLink, replacementFunction) else null,
                value.isOverride,
                value.buildTagInterval
        )
    }

    private fun <T> replaceBuildCommitLink(configuration: ServiceConfiguration, replacementFunction: Function<String, String>): ServiceConfiguration {
        val linkId = configuration.id
        @Suppress("UNCHECKED_CAST")
        val link = buildGitCommitLinkService.getLink(linkId) as BuildGitCommitLink<T>
        val linkData = link.parseData(configuration.data)
        val clonedData = link.clone(linkData) { replacementFunction.apply(it) }
        val node = link.toJson(clonedData)
        return ServiceConfiguration(
                linkId,
                node
        )
    }

    override fun onPropertyChanged(entity: ProjectEntity, value: GitBranchConfigurationProperty) {
        if (entity is Branch) {
            gitService.scheduleGitBuildSync(entity, value)
            gitService.getBranchConfiguration(entity)?.let { branchConfig ->
                searchIndexService.createSearchIndex(gitBranchSearchIndexer, GitBranchSearchItem(entity, branchConfig))
            }
        }
    }

    override fun onPropertyDeleted(entity: ProjectEntity, oldValue: GitBranchConfigurationProperty) {
        if (entity is Branch) {
            gitService.unscheduleGitBuildSync(entity, oldValue)
            searchIndexService.deleteSearchIndex(gitBranchSearchIndexer, entity.id())
        }
    }

}