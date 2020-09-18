package net.nemerosa.ontrack.extension.scm.catalog

import java.time.LocalDateTime

/**
 * Defines a repository / project entry as returned by a [SCMCatalogProvider].
 *
 * @param config Name of the associated SCM configuration in Ontrack
 * @param repository Name of the SCM repository (for example: "nemerosa/ontrack")
 * @param repositoryPage URL to the web repository page (a GitHub repository page for example)
 * @param lastActivity Timestamp for the last activity on this repository
 */
data class SCMCatalogSource(
        val config: String,
        val repository: String,
        val repositoryPage: String?,
        val lastActivity: LocalDateTime?
)
