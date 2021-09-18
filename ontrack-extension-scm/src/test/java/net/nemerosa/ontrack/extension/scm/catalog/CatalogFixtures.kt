package net.nemerosa.ontrack.extension.scm.catalog

import net.nemerosa.ontrack.common.Time

object CatalogFixtures {
    fun entry(
            scm: String = "test",
            config: String = "test-config",
            repository: String = "project/repository",
            teams: List<SCMCatalogTeam>? = null,
    ) = SCMCatalogEntry(
            config = config,
            repository = repository,
            repositoryPage = "uri:$repository",
            scm = scm,
            lastActivity = Time.now(),
            createdAt = Time.now(),
            timestamp = Time.now(),
            teams = teams,
    )

    fun team(
        id: String,
        name: String? = id,
        description: String? = null,
        url: String? = null,
        role: String? = null
    ) = SCMCatalogTeam(
        id,
        name,
        description,
        url,
        role,
    )
}