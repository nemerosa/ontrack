package net.nemerosa.ontrack.extension.stash.catalog

import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogEntry
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogProvider
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogSource
import net.nemerosa.ontrack.extension.scm.catalog.SCMLocation
import net.nemerosa.ontrack.extension.stash.client.BitbucketClientFactory
import net.nemerosa.ontrack.extension.stash.property.StashGitConfiguration
import net.nemerosa.ontrack.extension.stash.property.StashProjectConfigurationProperty
import net.nemerosa.ontrack.extension.stash.property.StashProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.stash.service.StashConfigurationService
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class BitbucketSCMCatalogProvider(
        private val stashConfigurationService: StashConfigurationService,
        private val bitbucketClientFactory: BitbucketClientFactory,
        private val propertyService: PropertyService
) : SCMCatalogProvider {

    override val id: String = "bitbucket"

    override val entries: List<SCMCatalogSource>
        get() = stashConfigurationService
                .configurations
                .flatMap { config ->
                    val client = bitbucketClientFactory.getBitbucketClient(config)
                    client.projects.flatMap { project ->
                        client.getRepositories(project)
                    }.map { repo ->
                        SCMCatalogSource(
                                config = config.name,
                                repository = "${repo.project}/${repo.repository}",
                                repositoryPage = StashProjectConfigurationProperty(config, repo.project, repo.repository, 0, null).repositoryUrl
                        )
                    }
                }

    override fun matches(entry: SCMCatalogEntry, project: Project): Boolean {
        val property: StashProjectConfigurationProperty? = propertyService.getProperty(project, StashProjectConfigurationPropertyType::class.java).value
        return property != null &&
                property.configuration.name == entry.config &&
                "${property.project}/${property.repository}" == entry.repository
    }

    override fun getSCMLocation(project: Project): SCMLocation? =
            propertyService.getProperty(project, StashProjectConfigurationPropertyType::class.java).value
                    ?.run {
                        val gitConfig = StashGitConfiguration(this, null)
                        SCMLocation(
                                scm = "bitbucket",
                                name = configuration.name,
                                uri = gitConfig.remote,
                                url = repositoryUrl
                        )
                    }
}