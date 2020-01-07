package net.nemerosa.ontrack.extension.github.catalog

import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClientFactory
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationProperty
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogEntry
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogProvider
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogSource
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class GitHubSCMCatalogProvider(
        private val gitHubConfigurationService: GitHubConfigurationService,
        private val gitHubClientFactory: OntrackGitHubClientFactory,
        private val propertyService: PropertyService
) : SCMCatalogProvider {

    override val id: String = "github"

    override val entries: List<SCMCatalogSource>
        get() = gitHubConfigurationService.configurations.flatMap { config ->
            getConfigEntries(config)
        }

    private fun getConfigEntries(config: GitHubEngineConfiguration): Iterable<SCMCatalogSource> {
        val client = gitHubClientFactory.create(config)
        return client.repositories.map { repo ->
            SCMCatalogSource(
                    config.name,
                    repo,
                    "${config.url}/$repo"
            )
        }
    }

    override fun matches(entry: SCMCatalogEntry, project: Project): Boolean {
        val property: GitHubProjectConfigurationProperty? = propertyService.getProperty(project, GitHubProjectConfigurationPropertyType::class.java).value
        return if (property != null) {
            property.configuration.name == entry.config &&
                    property.repository == entry.repository
        } else {
            false
        }
    }

}