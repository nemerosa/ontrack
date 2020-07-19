package net.nemerosa.ontrack.extension.stash.property

import net.nemerosa.ontrack.extension.git.model.GitConfiguration
import net.nemerosa.ontrack.extension.git.model.GitConfigurator
import net.nemerosa.ontrack.extension.git.model.GitPullRequest
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.extension.support.client.ClientConnection
import net.nemerosa.ontrack.extension.support.client.ClientFactory
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class StashConfigurator(
        private val propertyService: PropertyService,
        private val issueServiceRegistry: IssueServiceRegistry,
        private val clientFactory: ClientFactory
) : GitConfigurator {

    override fun isProjectConfigured(project: Project): Boolean {
        return propertyService.hasProperty(project, StashProjectConfigurationPropertyType::class.java)
    }

    override fun getConfiguration(project: Project): GitConfiguration? {
        return propertyService.getProperty(project, StashProjectConfigurationPropertyType::class.java)
                .value
                ?.run { getGitConfiguration(this) }
    }

    override fun toPullRequestID(key: String): Int? {
        if (key.isNotBlank()) {
            val m = "PR-(\\d+)".toRegex().matchEntire(key)
            if (m != null) {
                return m.groupValues[1].toInt(10)
            }
        }
        return null
    }

    override fun getPullRequest(configuration: GitConfiguration, id: Int): GitPullRequest? =
            if (configuration is StashGitConfiguration) {
                val client = clientFactory.getJsonClient(
                        ClientConnection(
                                configuration.property.configuration.url,
                                configuration.property.configuration.user,
                                configuration.property.configuration.password
                        )
                )
                val json = client.get("rest/api/1.0/projects/${configuration.property.project}/repos/${configuration.property.repository}/pull-requests/$id")
                GitPullRequest(
                        id = id,
                        key = "PR-$id",
                        source = json["fromRef"]["id"].asText(),
                        target = json["toRef"]["id"].asText(),
                        title = json["title"].asText()
                )
            } else {
                null
            }

    private fun getGitConfiguration(property: StashProjectConfigurationProperty): GitConfiguration {
        return StashGitConfiguration(
                property,
                getConfiguredIssueService(property)
        )
    }

    private fun getConfiguredIssueService(property: StashProjectConfigurationProperty): ConfiguredIssueService {
        return issueServiceRegistry.getConfiguredIssueService(
                property.issueServiceConfigurationIdentifier
        )
    }

}