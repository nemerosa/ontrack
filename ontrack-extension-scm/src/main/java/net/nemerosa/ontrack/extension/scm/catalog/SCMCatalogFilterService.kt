package net.nemerosa.ontrack.extension.scm.catalog

interface SCMCatalogFilterService {

    fun findCatalogEntries(filter: SCMCatalogFilter): List<SCMCatalogEntry>

}