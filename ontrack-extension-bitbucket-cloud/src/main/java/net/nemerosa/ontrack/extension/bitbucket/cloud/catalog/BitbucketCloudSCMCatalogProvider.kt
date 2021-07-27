package net.nemerosa.ontrack.extension.bitbucket.cloud.catalog

import net.nemerosa.ontrack.extension.bitbucket.cloud.client.BitbucketCloudClientFactory
import net.nemerosa.ontrack.extension.bitbucket.cloud.configuration.BitbucketCloudConfigurationService
import net.nemerosa.ontrack.extension.bitbucket.cloud.property.BitbucketCloudProjectConfigurationProperty
import net.nemerosa.ontrack.extension.bitbucket.cloud.property.BitbucketCloudProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogEntry
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogProvider
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogSource
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class BitbucketCloudSCMCatalogProvider(
    private val bitbucketCloudConfigurationService: BitbucketCloudConfigurationService,
    private val bitbucketCloudClientFactory: BitbucketCloudClientFactory,
    private val propertyService: PropertyService
) : SCMCatalogProvider {

    override val id: String = "bitbucket"

    override val entries: List<SCMCatalogSource>
        get() = bitbucketCloudConfigurationService
            .configurations
            .flatMap { config ->
                val client = bitbucketCloudClientFactory.getBitbucketCloudClient(config)
                client.repositories.mapNotNull { repo ->
                    val lastModified = client.getRepositoryLastModified(repo)
                    val creationDate = client.getRepositoryCreationDate(repo)
                    lastModified?.let {
                        SCMCatalogSource(
                            config = config.name,
                            repository = "${config.workspace}/${repo.slug}",
                            repositoryPage = BitbucketCloudProjectConfigurationProperty(
                                config,
                                repo.slug,
                                0,
                                null
                            ).repositoryUrl,
                            lastActivity = it,
                            createdAt = creationDate,
                        )
                    }
                }
            }

    override fun matches(entry: SCMCatalogEntry, project: Project): Boolean {
        val property: BitbucketCloudProjectConfigurationProperty? =
            propertyService.getProperty(project, BitbucketCloudProjectConfigurationPropertyType::class.java).value
        return property != null &&
                property.configuration.name == entry.config &&
                "${property.configuration.workspace}/${property.repository}" == entry.repository
    }
}