package net.nemerosa.ontrack.extension.scm.catalog

interface SCMCatalogFilterService {

    /**
     * Finds a consolidated list of SCM catalog entries, together with their project, or of projects unlinked to any valid SCM catalog entry.
     */
    fun findCatalogProjectEntries(filter: SCMCatalogProjectFilter): List<SCMCatalogEntryOrProject>

}