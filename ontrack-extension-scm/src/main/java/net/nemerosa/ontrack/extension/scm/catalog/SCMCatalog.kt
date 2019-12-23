package net.nemerosa.ontrack.extension.scm.catalog

interface SCMCatalog {

    /**
     * Collects all catalog entries
     */
    fun collectSCMCatalog(logger: (String) -> Unit)

    /**
     * Gets a stream of catalog entries to work with.
     */
    val catalogEntries: Sequence<SCMCatalogEntry>

}