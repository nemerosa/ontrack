package net.nemerosa.ontrack.extension.scm.catalog

import java.time.LocalDate

/**
 * Filter used on the SCM catalog entries
 */
data class SCMCatalogProjectFilter(
        val offset: Int = 0,
        val size: Int = 20,
        val scm: String? = null,
        val config: String? = null,
        val repository: String? = null,
        val project: String? = null,
        val link: SCMCatalogProjectFilterLink = SCMCatalogProjectFilterLink.ALL,
        val beforeLastActivity: LocalDate? = null,
        val afterLastActivity: LocalDate? = null,
        val sortOn: SCMCatalogProjectFilterSort? = null,
        val sortAscending: Boolean = true
)
