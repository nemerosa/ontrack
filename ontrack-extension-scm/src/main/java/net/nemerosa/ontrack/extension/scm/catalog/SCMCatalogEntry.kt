package net.nemerosa.ontrack.extension.scm.catalog

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDateTime

/**
 * Defines a repository / project entry in the SCM catalog.
 *
 * @param scm Type of SCM ("bitbucket", "github", etc.) - linked to [SCMCatalogProvider.id]
 * @param config Name of the associated SCM configuration in Ontrack
 * @param repository Name of the SCM repository (for example: "nemerosa/ontrack")
 * @param repositoryPage URL to the web repository page (a GitHub repository page for example)
 * @param lastActivity Timestamp for the last activity on this repository
 * @param timestamp Timestamp for the collection of the information
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class SCMCatalogEntry(
        val scm: String,
        val config: String,
        val repository: String,
        val repositoryPage: String?,
        val lastActivity: LocalDateTime?,
        val timestamp: LocalDateTime
) : Comparable<SCMCatalogEntry> {
    @get:JsonIgnore
    val key: String
        get() = "$scm::$config::$repository"

    override fun compareTo(other: SCMCatalogEntry): Int =
            compareValuesBy(this, other,
                    { it.scm },
                    { it.config },
                    { it.repository }
            )
}
