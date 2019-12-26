package net.nemerosa.ontrack.extension.scm.catalog

import net.nemerosa.ontrack.common.Time

object CatalogFixtures {
    fun entry(scm: String = "test") = SCMCatalogEntry(
            config = "test-config",
            repository = "project/repository",
            repositoryPage = "uri:project/repository",
            scm = scm,
            timestamp = Time.now()
    )
}