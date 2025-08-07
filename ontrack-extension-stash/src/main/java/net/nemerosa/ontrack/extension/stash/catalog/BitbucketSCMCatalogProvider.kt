package net.nemerosa.ontrack.extension.stash.catalog

import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogEntry
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogProvider
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogSource
import net.nemerosa.ontrack.extension.stash.client.BitbucketClientFactory
import net.nemerosa.ontrack.extension.stash.property.StashProjectConfigurationProperty
import net.nemerosa.ontrack.extension.stash.property.StashProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.stash.service.StashConfigurationService
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class BitbucketSCMCatalogProvider(
        private val stashConfigurationService: StashConfigurationService,
        private val bitbucketClientFactory: BitbucketClientFactory,
        private val propertyService: PropertyService
) : SCMCatalogProvider {

    private val logger: Logger = LoggerFactory.getLogger(BitbucketSCMCatalogProvider::class.java)

    override val id: String = "bitbucket"

    override val entries: List<SCMCatalogSource>
        get() = stashConfigurationService
                .configurations
                .flatMap { config ->
                    val client = bitbucketClientFactory.getBitbucketClient(config)
                    client.projects.flatMap { project ->
                        client.getRepositories(project)
                    }.mapNotNull { repo ->
                        val lastModified = try {
                            client.getRepositoryLastModified(repo)
                        } catch (_: Exception) {
                            logger.debug("Cannot get last modified date on Bitbucket repository at {}", repo)
                            null
                        }
                        lastModified?.let {
                            SCMCatalogSource(
                                    config = config.name,
                                    repository = "${repo.project}/${repo.repository}",
                                    repositoryPage = StashProjectConfigurationProperty(config, repo.project, repo.repository, 0, null).repositoryUrl,
                                    createdAt = null, // Cannot find a REST API end point to get the creation date
                                    lastActivity = it
                            )
                        }
                    }
                }

    override fun matches(entry: SCMCatalogEntry, project: Project): Boolean {
        val property: StashProjectConfigurationProperty? = propertyService.getProperty(project, StashProjectConfigurationPropertyType::class.java).value
        return property != null &&
                property.configuration.name == entry.config &&
                "${property.project}/${property.repository}" == entry.repository
    }

    override fun toProjectName(scmRepository: String): String =
        scmRepository.substringAfter("/")

    override fun linkProjectToSCM(project: Project, entry: SCMCatalogEntry): Boolean {
        val config = stashConfigurationService.findConfiguration(entry.config) ?: return false
        val projectName = entry.repository.substringBefore("/")
        val repositoryName = entry.repository.substringAfter("/")
        propertyService.editProperty(
            project,
            StashProjectConfigurationPropertyType::class.java,
            StashProjectConfigurationProperty(
                config,
                projectName,
                repositoryName,
                0,
                null
            )
        )
        return true
    }
}