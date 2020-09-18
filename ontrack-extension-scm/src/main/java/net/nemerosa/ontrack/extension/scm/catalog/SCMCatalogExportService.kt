package net.nemerosa.ontrack.extension.scm.catalog

import net.nemerosa.ontrack.common.Document

/**
 * Service to export the list of SCM Catalog entries.
 */
interface SCMCatalogExportService {

    /**
     * Exporting the list of SCM Catalog entries as a CSV.
     */
    fun exportCatalogAsCSV(entries: List<SCMCatalogEntry>): Document

}