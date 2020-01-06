package net.nemerosa.ontrack.extension.scm.catalog

/**
 * Filter used on the SCM catalog entries
 */
data class SCMCatalogFilter(
        val offset: Int = 0,
        val size: Int = 20,
        val scm: String? = null,
        val config: String? = null,
        val repository: String? = null,
        val link: SCMCatalogFilterLink = SCMCatalogFilterLink.ALL
)
