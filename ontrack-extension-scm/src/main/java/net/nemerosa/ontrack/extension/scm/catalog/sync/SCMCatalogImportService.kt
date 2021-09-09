package net.nemerosa.ontrack.extension.scm.catalog.sync

interface SCMCatalogImportService {

    fun importCatalog(logger: (String) -> Unit)

}