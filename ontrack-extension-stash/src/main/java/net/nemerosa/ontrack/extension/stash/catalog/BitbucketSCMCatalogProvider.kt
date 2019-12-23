package net.nemerosa.ontrack.extension.stash.catalog

import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogProvider
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogSource
import net.nemerosa.ontrack.extension.stash.client.BitbucketClientFactory
import net.nemerosa.ontrack.extension.stash.service.StashConfigurationService
import org.springframework.stereotype.Component

@Component
class BitbucketSCMCatalogProvider(
        private val stashConfigurationService: StashConfigurationService,
        private val bitbucketClientFactory: BitbucketClientFactory
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
                                repository = "${repo.project}/${repo.repository}"
                        )
                    }
                }
}