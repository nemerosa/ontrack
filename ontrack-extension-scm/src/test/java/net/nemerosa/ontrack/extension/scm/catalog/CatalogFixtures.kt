package net.nemerosa.ontrack.extension.scm.catalog

import net.nemerosa.ontrack.common.Time

object CatalogFixtures {
    fun entry(
            scm: String = "test",
            config: String = "test-config",
            repository: String = "project/repository"
    ) = SCMCatalogEntry(
            config = config,
            repository = repository,
            repositoryPage = "uri:$repository",
            scm = scm,
            timestamp = Time.now()
    )
}